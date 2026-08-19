package com.hostflow.security.product;

/**
 * Mirrors the "product_scope" values assigned on Keycloak user attributes.
 * PLATFORM is a wildcard scope — holders (xanuos-console operators) get access
 * to every product, used by ProductAccessGuard below.
 */
public enum ProductScope {
    XANUOS,
    NAZILCO,
    PLATFORM;

    /**
     * Convert a string to a ProductScope enum value.
     * 
     * @param scope the scope string (case insensitive)
     * @return the ProductScope enum value, or null if not found
     */
    public static ProductScope fromString(String scope) {
        if (scope == null || scope.isEmpty()) {
            return null;
        }
        try {
            return ProductScope.valueOf(scope.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Check if this scope grants access to the required scope.
     * PLATFORM is a wildcard that has access to everything.
     * 
     * @param required the scope required for access
     * @return true if this scope has access to the required scope
     */
    public boolean hasAccessTo(ProductScope required) {
        if (required == null) {
            return true;
        }
        return this == required || this == ProductScope.PLATFORM;
    }

    /**
     * Check if this scope is a platform admin scope.
     * 
     * @return true if this is the PLATFORM scope
     */
    public boolean isPlatform() {
        return this == ProductScope.PLATFORM;
    }

    /**
     * Get the display name for this scope.
     * 
     * @return human-readable display name
     */
    public String getDisplayName() {
        return switch (this) {
            case XANUOS -> "XanuOS Property Management";
            case NAZILCO -> "NazilCo Customer Platform";
            case PLATFORM -> "HostFlow Platform Admin";
        };
    }

    /**
     * Get the client ID associated with this product scope.
     * 
     * @return the Keycloak client ID for this product
     */
    public String getClientId() {
        return switch (this) {
            case XANUOS -> "xanuos-app";
            case NAZILCO -> "nazilco-app";
            case PLATFORM -> "hostflow-admin-cli";
        };
    }

    /**
     * Get the audience for this product scope.
     * 
     * @return the audience value for JWT tokens
     */
    public String getAudience() {
        return "hostflow-" + this.name().toLowerCase();
    }

    /**
     * Convert to a Spring Security authority string.
     * 
     * @return authority string (e.g., "PRODUCT_XANUOS")
     */
    public String getAuthority() {
        return "PRODUCT_" + this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
