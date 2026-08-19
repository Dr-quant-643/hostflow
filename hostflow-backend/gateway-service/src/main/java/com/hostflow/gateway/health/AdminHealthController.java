package com.hostflow.gateway.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AdminHealthController {

    private final AdminHealthService healthService;

    public AdminHealthController(AdminHealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/admin/health")
    public Mono<AggregatedHealthResponse> health() {
        return healthService.checkAll();
    }
}
