package com.hostflow.identity.entity;

/**
 * Mirrors the realm roles defined in core-security's hostflow-realm-export.json.
 * Kept as a matching Java enum here so module-identity can validate/display roles
 * without parsing raw strings from Keycloak responses.
 */
public enum UserRole {
    XANUOS_OWNER,
    XANUOS_MANAGER,
    XANUOS_STAFF,
    NAZILCO_CUSTOMER,
    PLATFORM_ADMIN
}
