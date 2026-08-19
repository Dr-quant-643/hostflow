package com.hostflow.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HostFlowJwtAuthenticationConverterTest {

    private final HostFlowJwtAuthenticationConverter converter = new HostFlowJwtAuthenticationConverter();

    @Test
    void convert_mapsRealmRolesToRoleAuthorities() {
        Jwt jwt = buildJwt(
                Map.of("roles", List.of("xanuos_manager", "platform_admin")),
                List.of("XANUOS", "NAZILCO")
        );

        AbstractAuthenticationToken token = converter.convert(jwt);

        assertThat(authorityNames(token))
                .contains("ROLE_XANUOS_MANAGER", "ROLE_PLATFORM_ADMIN");
    }

    @Test
    void convert_mapsProductScopeToProductAuthorities() {
        Jwt jwt = buildJwt(
                Map.of("roles", List.of("nazilco_customer")),
                List.of("NAZILCO")
        );

        AbstractAuthenticationToken token = converter.convert(jwt);

        assertThat(authorityNames(token)).contains("PRODUCT_NAZILCO");
    }

    @Test
    void convert_withNoRealmAccessClaim_producesNoRoleAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(UUID.randomUUID().toString())
                .claim("tenant_id", UUID.randomUUID().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        AbstractAuthenticationToken token = converter.convert(jwt);

        assertThat(authorityNames(token)).isEmpty();
    }

    @Test
    void convert_retainsJwtAsPrincipal_forDownstreamTenantResolution() {
        Jwt jwt = buildJwt(Map.of("roles", List.of("xanuos_owner")), List.of("XANUOS"));

        AbstractAuthenticationToken token = converter.convert(jwt);

        assertThat(token.getPrincipal()).isInstanceOf(Jwt.class);
    }

    private Jwt buildJwt(Map<String, Object> realmAccess, List<String> productScopes) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(UUID.randomUUID().toString())
                .claim("tenant_id", UUID.randomUUID().toString())
                .claim("realm_access", realmAccess)
                .claim("product_scope", productScopes)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();
    }

    private List<String> authorityNames(AbstractAuthenticationToken token) {
        return token.getAuthorities().stream().map(Object::toString).toList();
    }
}
