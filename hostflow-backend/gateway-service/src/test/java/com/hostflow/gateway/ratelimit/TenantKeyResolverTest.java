package com.hostflow.gateway.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

class TenantKeyResolverTest {

    private final TenantKeyResolver resolver = new TenantKeyResolver();

    @Test
    void resolve_returnsTenantPrefixedKey_whenJwtHasTenantId() {
        UUID tenantId = UUID.randomUUID();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("tenant_id", tenantId.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, List.of());

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/properties").build());
        exchange.getAttributes().put("org.springframework.security.web.server.SecurityWebFilterChain", authToken);

        // Principal is supplied via exchange.getPrincipal() in real Gateway flow;
        // constructing it directly here to test resolve() logic in isolation.
        StepVerifier.create(resolver.resolve(withPrincipal(exchange, authToken)))
                .expectNext("tenant:" + tenantId)
                .verifyComplete();
    }

    @Test
    void resolve_fallsBackToRemoteAddress_whenNoAuthenticatedPrincipal() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/properties")
                .remoteAddress(new InetSocketAddress("192.168.1.10", 54321))
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("ip:192.168.1.10")
                .verifyComplete();
    }

    private ServerWebExchange withPrincipal(ServerWebExchange exchange, JwtAuthenticationToken token) {
        return exchange.mutate()
                .principal(reactor.core.publisher.Mono.just(token))
                .build();
    }
}
