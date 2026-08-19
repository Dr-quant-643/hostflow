package com.hostflow.gateway.health;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proxies app's own /actuator/health (which already aggregates Postgres, Redis,
 * RabbitMQ connectivity via Spring Boot Actuator's built-in health indicators —
 * per app's application-dev.yml management.health config) and re-labels it, so
 * xanuos-console only ever needs to know about the gateway, never the internal
 * network address of app or any future extracted microservice. This is the
 * layering decision made during reconciliation item 4.
 *
 * "gateway" itself is always reported UP if this method runs at all (if the
 * gateway were down, there'd be no response to return in the first place) —
 * kept
 * as an explicit entry for symmetry/clarity in the response shape, not because
 * it's a meaningful runtime check.
 */
@Service
@EnableConfigurationProperties(AdminHealthProperties.class)
public class AdminHealthService {

    private final WebClient webClient;
    private final AdminHealthProperties properties;

    public AdminHealthService(AdminHealthProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public Mono<AggregatedHealthResponse> checkAll() {
        return checkAppService()
                .map(appHealth -> {
                    Map<String, AggregatedHealthResponse.ComponentHealth> components = new LinkedHashMap<>();
                    components.put("gateway", new AggregatedHealthResponse.ComponentHealth("UP", "responding"));
                    components.putAll(appHealth);

                    boolean allUp = components.values().stream()
                            .allMatch(c -> "UP".equals(c.status()));

                    return new AggregatedHealthResponse(allUp ? "UP" : "DEGRADED", Instant.now(), components);
                });
    }

    private Mono<Map<String, AggregatedHealthResponse.ComponentHealth>> checkAppService() {
        return webClient.get()
                .uri(properties.getAppServiceUri() + "/actuator/health")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .map(this::parseAppHealth)
                .onErrorResume(e -> Mono.just(Map.of(
                        "app",
                        new AggregatedHealthResponse.ComponentHealth("DOWN", "unreachable: " + e.getMessage()))));
    }

    private Map<String, AggregatedHealthResponse.ComponentHealth> parseAppHealth(Map<String, Object> body) {
        Map<String, AggregatedHealthResponse.ComponentHealth> result = new LinkedHashMap<>();

        String appStatus = String.valueOf(body.getOrDefault("status", "UNKNOWN"));
        result.put("app", new AggregatedHealthResponse.ComponentHealth(appStatus, "reported by app/actuator/health"));

        Object componentsObj = body.get("components");
        if (componentsObj instanceof Map<?, ?> componentsMap) {
            for (Map.Entry<?, ?> entry : componentsMap.entrySet()) {
                String name = String.valueOf(entry.getKey());
                if (entry.getValue() instanceof Map<?, ?> detail) {
                    Object statusValue = detail.get("status");
                    String status = statusValue != null ? String.valueOf(statusValue) : "UNKNOWN";
                    result.put(name.toLowerCase(),
                            new AggregatedHealthResponse.ComponentHealth(status, "from app/actuator/health"));
                }
            }
        }

        return result;
    }
}
