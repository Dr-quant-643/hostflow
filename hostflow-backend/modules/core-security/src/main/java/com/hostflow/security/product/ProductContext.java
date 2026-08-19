package com.hostflow.security.product;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ThreadLocal holder for the current request's product scope(s), set by
 * JwtTenantResolvingFilter from the JWT's product_scope claim. A user can hold
 * more than one scope (e.g. a platform admin), so this holds a Set rather than
 * a single value.
 */
public final class ProductContext {

    private static final ThreadLocal<Set<ProductScope>> CURRENT_SCOPES = new ThreadLocal<>();

    private ProductContext() {
        // Private constructor to prevent instantiation
    }

    public static void set(Set<ProductScope> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            CURRENT_SCOPES.set(Collections.emptySet());
        } else {
            CURRENT_SCOPES.set(new HashSet<>(scopes));
        }
    }

    public static void set(ProductScope scope) {
        if (scope == null) {
            CURRENT_SCOPES.set(Collections.emptySet());
        } else {
            CURRENT_SCOPES.set(Collections.singleton(scope));
        }
    }

    public static Set<ProductScope> get() {
        Set<ProductScope> scopes = CURRENT_SCOPES.get();
        return scopes == null ? Collections.emptySet() : Collections.unmodifiableSet(scopes);
    }

    public static ProductScope getPrimary() {
        Set<ProductScope> scopes = get();
        if (scopes.isEmpty()) {
            return null;
        }
        if (scopes.contains(ProductScope.PLATFORM)) {
            return ProductScope.PLATFORM;
        }
        return scopes.stream().sorted().findFirst().orElse(null);
    }

    public static boolean hasAccess(ProductScope required) {
        if (required == null) {
            return true;
        }
        Set<ProductScope> scopes = get();
        return scopes.contains(required) || scopes.contains(ProductScope.PLATFORM);
    }

    public static boolean hasAnyAccess(Set<ProductScope> requiredScopes) {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return true;
        }
        Set<ProductScope> current = get();
        if (current.contains(ProductScope.PLATFORM)) {
            return true;
        }
        for (ProductScope required : requiredScopes) {
            if (current.contains(required)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllAccess(Set<ProductScope> requiredScopes) {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return true;
        }
        Set<ProductScope> current = get();
        if (current.contains(ProductScope.PLATFORM)) {
            return true;
        }
        return current.containsAll(requiredScopes);
    }

    public static boolean hasAccess(String requiredScope) {
        if (requiredScope == null || requiredScope.isEmpty()) {
            return true;
        }
        ProductScope scope = ProductScope.fromString(requiredScope);
        return scope != null && hasAccess(scope);
    }

    public static boolean isPlatformAdmin() {
        return get().contains(ProductScope.PLATFORM);
    }

    public static boolean isSet() {
        return !get().isEmpty();
    }

    public static void requireAccess(ProductScope required) {
        if (!hasAccess(required)) {
            throw new ProductAccessDeniedException(required);
        }
    }

    public static void requireAnyAccess(Set<ProductScope> requiredScopes) {
        if (!hasAnyAccess(requiredScopes)) {
            throw new ProductAccessDeniedException(requiredScopes);
        }
    }

    public static void clear() {
        CURRENT_SCOPES.remove();
    }

    public static void runWithScopes(Set<ProductScope> scopes, Runnable runnable) {
        try {
            set(scopes);
            runnable.run();
        } finally {
            clear();
        }
    }

    public static void runWithScope(ProductScope scope, Runnable runnable) {
        try {
            set(scope);
            runnable.run();
        } finally {
            clear();
        }
    }

    public static <T> T runWithScopes(Set<ProductScope> scopes, java.util.function.Supplier<T> supplier) {
        try {
            set(scopes);
            return supplier.get();
        } finally {
            clear();
        }
    }

    public static String getScopesAsString() {
        Set<ProductScope> scopes = get();
        if (scopes.isEmpty()) {
            return "";
        }
        return String.join(",", scopes.stream()
                .map(Enum::name)
                .sorted()
                .toArray(String[]::new));
    }
}
