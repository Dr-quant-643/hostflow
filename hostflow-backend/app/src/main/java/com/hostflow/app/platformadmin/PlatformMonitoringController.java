package com.hostflow.app.platformadmin;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class PlatformMonitoringController {

    private final PlatformMonitoringQueries queries;

    public PlatformMonitoringController(PlatformMonitoringQueries queries) {
        this.queries = queries;
    }

    @GetMapping("/api/v1/admin/monitoring")
    public ResponseEntity<ApiResponse<PlatformMonitoringQueries.MonitoringSnapshot>> snapshot() {
        return ResponseEntity.ok(ApiResponse.success(queries.snapshot()));
    }
}
