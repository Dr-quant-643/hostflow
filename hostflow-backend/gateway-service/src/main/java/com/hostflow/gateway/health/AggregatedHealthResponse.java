package com.hostflow.gateway.health;

import java.time.Instant;
import java.util.Map;

public record AggregatedHealthResponse(String overallStatus, Instant checkedAt,
        Map<String, ComponentHealth> components) {

    public record ComponentHealth(String status, String detail) {
    }
}
