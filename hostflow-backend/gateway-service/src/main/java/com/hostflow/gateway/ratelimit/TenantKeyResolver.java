package com.hostflow.gateway.ratelimit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Resolves the Redis rate-limit bucket key from the request's tenant_id claim,
 * so
 * rate limits are enforced PER TENANT rather than globally — one noisy tenant
 * cannot
 * exhaust the rate limit budget for every other tenant on the platform.
 *
 * Falls back to the client's remote address for unauthenticated requests (e.g.
 * the
 * login endpoint itself, before a JWT exists), so those paths are still
 * rate-limited,
 * just not on a per-tenant basis.
 */
@Component
public class TenantKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(this::extractTenantKey)
                .switchIfEmpty(Mono.just(resolveByRemoteAddress(exchange)));
    }

    private String extractTenantKey(Jwt jwt) {
        String tenantId = jwt.getClaimAsString("tenant_id");
        return (tenantId != null && !tenantId.isBlank()) ? "tenant:" + tenantId : "unauthenticated";
    }

    private String resolveByRemoteAddress(ServerWebExchange exchange) {
        if (exchange.getRequest().getRemoteAddress() == null) {
            return "unknown";
        }
        return "ip:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}
