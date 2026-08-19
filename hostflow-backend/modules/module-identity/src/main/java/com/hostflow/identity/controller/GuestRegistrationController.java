package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.GuestProfileResponse;
import com.hostflow.identity.dto.RegisterGuestRequest;
import com.hostflow.identity.entity.GuestProfile;
import com.hostflow.identity.service.GuestRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * No @PreAuthorize — this is the one identity-creation endpoint that must be
 * reachable by a completely anonymous visitor. Permitted explicitly in
 * SecurityConfig.
 */
@RestController
public class GuestRegistrationController {

    private final GuestRegistrationService registrationService;

    public GuestRegistrationController(GuestRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/api/v1/guests/register")
    public ResponseEntity<ApiResponse<GuestProfileResponse>> register(
            @Valid @RequestBody RegisterGuestRequest request) {
        GuestProfile profile = registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(GuestProfileResponse.from(profile)));
    }
}
