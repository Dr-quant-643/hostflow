package com.hostflow.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RouteConfig {

    // RouteLocatorBuilder's .uri(String) takes a literal URI — it does not
    // resolve ${...} placeholders itself (that syntax only works in
    // YAML-defined routes), so the app-service URI must be resolved via
    // @Value before being handed to the builder. Passing the raw
    // "${hostflow.gateway.app-service-uri:...}" string through unresolved
    // only surfaced once a route was actually matched and Spring Cloud
    // Gateway tried to parse it as a URI.
    @Value("${hostflow.gateway.app-service-uri:http://app-service:8080}")
    private String appServiceUri;

    @Bean
    public RouteLocator hostFlowRoutes(RouteLocatorBuilder builder,
                                        RedisRateLimiter redisRateLimiter,
                                        KeyResolver tenantKeyResolver) {
        return builder.routes()
                .route("app-service", r -> r
                        .path("/api/**")
                        .filters(f -> f.requestRateLimiter(rl -> rl
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(tenantKeyResolver)))
                        .uri(appServiceUri))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(50, 100, 1);
    }
}
