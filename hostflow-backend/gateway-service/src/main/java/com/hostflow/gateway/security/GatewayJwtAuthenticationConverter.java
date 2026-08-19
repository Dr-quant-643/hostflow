package com.hostflow.gateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reactive equivalent of core-security's HostFlowJwtAuthenticationConverter —
 * this was a genuine gap in the original gateway-service build (it relied on
 * Spring Security's default reactive JWT converter, which does not understand
 * Keycloak's realm_access.roles claim shape). Duplicated here rather than
 * shared
 * as a common module because core-security's version implements the
 * Servlet-stack
 * Converter interface, which is fundamentally incompatible with WebFlux's
 * reactive
 * equivalent — this is the same Servlet-vs-reactive split already documented in
 * gateway-service's pom.xml exclusions.
 */
@Component
public class GatewayJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(extractRealmRoleAuthorities(jwt));
        authorities.addAll(extractProductScopeAuthorities(jwt));
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
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
