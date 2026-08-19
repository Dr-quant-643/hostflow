package com.hostflow.security.product;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Two ways to enforce product scoping across future modules:
 * 1. Declarative: @PreAuthorize("hasAuthority('PRODUCT_XANUOS')") on a
 * controller method
 * (authority is set by HostFlowJwtAuthenticationConverter)
 * 2. Imperative: inject this guard and call assertAccess(ProductScope.XANUOS)
 * inside a
 * service method, useful when the check depends on runtime data, not just the
 * endpoint.
 */
@Component
public class ProductAccessGuard {

    public void assertAccess(ProductScope required) {
        if (required == null) {
            return;
        }
        if (!ProductContext.hasAccess(required)) {
            throw new ProductAccessDeniedException(required);
        }
    }

    public void assertAccess(String requiredScope) {
        if (requiredScope == null || requiredScope.isEmpty()) {
            return;
        }
        ProductScope scope = ProductScope.fromString(requiredScope);
        if (scope == null) {
            throw new ProductAccessDeniedException("Invalid product scope: " + requiredScope);
        }
        assertAccess(scope);
    }

    public void assertAnyAccess(Set<ProductScope> requiredScopes) {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return;
        }
        if (!ProductContext.hasAnyAccess(requiredScopes)) {
            throw new ProductAccessDeniedException(requiredScopes);
        }
    }

    public void assertAllAccess(Set<ProductScope> requiredScopes) {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return;
        }
        if (!ProductContext.hasAllAccess(requiredScopes)) {
            throw new ProductAccessDeniedException(
                    String.format("This action requires all of: %s",
                            String.join(", ", requiredScopes.stream()
                                    .map(Enum::name)
                                    .sorted()
                                    .toArray(String[]::new))));
        }
    }

    public boolean hasAccess(ProductScope required) {
        return ProductContext.hasAccess(required);
    }

    public boolean hasAccess(String requiredScope) {
        return ProductContext.hasAccess(requiredScope);
    }

    public boolean hasAnyAccess(Set<ProductScope> requiredScopes) {
        return ProductContext.hasAnyAccess(requiredScopes);
    }

    public boolean hasAllAccess(Set<ProductScope> requiredScopes) {
        return ProductContext.hasAllAccess(requiredScopes);
    }

    public ProductScope getCurrentUserScope() {
        return ProductContext.getPrimary();
    }

    public Set<ProductScope> getCurrentUserScopes() {
        return ProductContext.get();
    }

    public boolean isPlatformAdmin() {
        return ProductContext.isPlatformAdmin();
    }
}
