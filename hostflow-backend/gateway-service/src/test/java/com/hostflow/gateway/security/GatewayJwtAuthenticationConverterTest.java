package com.hostflow.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayJwtAuthenticationConverterTest {

    private final GatewayJwtAuthenticationConverter converter = new GatewayJwtAuthenticationConverter();

    @Test
    void convert_mapsPlatformAdminRealmRole_toRoleAuthority() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(UUID.randomUUID().toString())
                .claim("realm_access", Map.of("roles", List.of("platform_admin")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        StepVerifier.create(converter.convert(jwt))
                .assertNext((AbstractAuthenticationToken token) -> assertThat(
                        token.getAuthorities().stream().map(Object::toString).toList())
                        .contains("ROLE_PLATFORM_ADMIN"))
                .verifyComplete();
    }
}
