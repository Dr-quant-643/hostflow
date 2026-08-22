package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.OrganizationResponse;
import com.hostflow.identity.dto.RegisterHostRequest;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.service.OrganizationOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * No @PreAuthorize -- self-service XanuOS signup, the property-manager
 * counterpart to GuestRegistrationController. Permitted explicitly in
 * SecurityConfig.
 */
@RestController
public class HostSelfSignupController {

    private final OrganizationOnboardingService onboardingService;

    public HostSelfSignupController(OrganizationOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/api/v1/hosts/register")
    public ResponseEntity<ApiResponse<OrganizationResponse>> register(
            @Valid @RequestBody RegisterHostRequest request) {
        Organization organization = onboardingService.selfSignup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrganizationResponse.from(organization)));
    }
}
