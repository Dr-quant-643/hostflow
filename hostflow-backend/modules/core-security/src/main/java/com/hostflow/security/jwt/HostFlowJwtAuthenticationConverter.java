package com.hostflow.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts a validated Keycloak Jwt into Spring authorities:
 * - realm_access.roles -> "ROLE_<UPPERCASED_ROLE>"
 * (so @PreAuthorize("hasRole('XANUOS_MANAGER')") works)
 * - product_scope claim -> "PRODUCT_<SCOPE>"
 * (so @PreAuthorize("hasAuthority('PRODUCT_XANUOS')") works)
 *
 * tenant_id is deliberately NOT turned into an authority here — it's not a
 * permission,
 * it's a data-scoping value, and is instead picked up by
 * JwtTenantResolvingFilter and
 * placed into TenantContext for the RLS/SET LOCAL mechanism built in
 * core-tenancy.
 */
@Component
public class HostFlowJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(extractRealmRoleAuthorities(jwt));
        authorities.addAll(extractProductScopeAuthorities(jwt));

        return new JwtAuthenticationToken(jwt, authorities);
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractRealmRoleAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return List.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .toList();
    }

    private List<GrantedAuthority> extractProductScopeAuthorities(Jwt jwt) {
        List<String> productScopes = jwt.getClaimAsStringList("product_scope");
        if (productScopes == null) {
            return List.of();
        }

        return productScopes.stream()
                .map(scope -> (GrantedAuthority) new SimpleGrantedAuthority("PRODUCT_" + scope.toUpperCase()))
                .toList();
    }
}
