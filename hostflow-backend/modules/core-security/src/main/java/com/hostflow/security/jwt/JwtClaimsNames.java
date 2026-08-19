package com.hostflow.security.jwt;

/**
 * Names of the custom claims configured on the Keycloak client (see
 * src/main/resources/keycloak/hostflow-realm-export.json protocol mappers).
 * Centralized here so no module references a raw string claim name directly.
 */
public final class JwtClaimsNames {

    public static final String TENANT_ID = "tenant_id";
    public static final String PRODUCT_SCOPE = "product_scope";
    public static final String REALM_ACCESS = "realm_access";
    public static final String REALM_ACCESS_ROLES = "roles";

    private JwtClaimsNames() {
    }
}
