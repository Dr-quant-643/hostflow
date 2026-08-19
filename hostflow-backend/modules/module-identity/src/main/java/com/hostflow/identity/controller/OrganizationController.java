package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.OnboardOrganizationRequest;
import com.hostflow.identity.dto.OrganizationResponse;
import com.hostflow.identity.dto.RenameOrganizationRequest;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.service.OrganizationOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class OrganizationController {

    private final OrganizationOnboardingService onboardingService;

    public OrganizationController(OrganizationOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> onboard(@Valid @RequestBody OnboardOrganizationRequest request) {
        Organization organization = onboardingService.onboard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(OrganizationResponse.from(organization)));
    }

    @PatchMapping("/{orgId}/rename")
    public ResponseEntity<ApiResponse<OrganizationResponse>> rename(
            @PathVariable UUID orgId, @Valid @RequestBody RenameOrganizationRequest request) {
        Organization organization = onboardingService.rename(orgId, request.name());
        return ResponseEntity.ok(ApiResponse.success(OrganizationResponse.from(organization)));
    }
}
