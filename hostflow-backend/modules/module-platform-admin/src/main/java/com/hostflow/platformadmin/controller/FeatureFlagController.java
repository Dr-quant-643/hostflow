package com.hostflow.platformadmin.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.platformadmin.dto.FeatureFlagResponse;
import com.hostflow.platformadmin.dto.SetFeatureFlagRequest;
import com.hostflow.platformadmin.service.FeatureFlagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/feature-flags")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class FeatureFlagController {

    private final FeatureFlagService service;

    public FeatureFlagController(FeatureFlagService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeatureFlagResponse>>> listAll() {
        List<FeatureFlagResponse> flags = service.listAll().stream().map(FeatureFlagResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(flags));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<FeatureFlagResponse>> setGlobal(@Valid @RequestBody SetFeatureFlagRequest request) {
        var flag = service.setGlobalFlag(request.key(), request.enabled(), request.description());
        return ResponseEntity.ok(ApiResponse.success(FeatureFlagResponse.from(flag)));
    }

    @PutMapping("/org/{orgId}")
    public ResponseEntity<ApiResponse<FeatureFlagResponse>> setOrgOverride(
            @PathVariable UUID orgId, @RequestParam String key, @RequestParam boolean enabled) {
        var flag = service.setOrgOverride(key, orgId, enabled);
        return ResponseEntity.ok(ApiResponse.success(FeatureFlagResponse.from(flag)));
    }
}
