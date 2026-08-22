package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.ClaimWorkspaceRequest;
import com.hostflow.identity.dto.OrganizationResponse;
import com.hostflow.identity.entity.Organization;
import com.hostflow.identity.service.OrganizationOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * For a Keycloak identity that already exists (typically created via
 * "Continue with Google") but has no tenant_id/product_scope yet -- the
 * counterpart to HostSelfSignupController for a caller who is already
 * authenticated. Only requires isAuthenticated(), deliberately not
 * PRODUCT_XANUOS, since lacking that authority is exactly the situation this
 * endpoint exists to fix. The caller's session cookie will still carry the
 * OLD claims after this succeeds -- the frontend must refresh the token
 * afterward for PRODUCT_XANUOS to actually take effect.
 */
@RestController
public class HostWorkspaceClaimController {

    private final OrganizationOnboardingService onboardingService;

    public HostWorkspaceClaimController(OrganizationOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/api/v1/hosts/claim-workspace")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrganizationResponse>> claim(
            @Valid @RequestBody ClaimWorkspaceRequest request, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String fullName = jwt.getClaimAsString("name");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        if (firstName == null || lastName == null) {
            String[] parts = splitName(fullName != null ? fullName : email);
            firstName = firstName != null ? firstName : parts[0];
            lastName = lastName != null ? lastName : parts[1];
        }

        Organization organization = onboardingService.claimWorkspace(
                jwt.getSubject(), email, firstName, lastName, request.organizationName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrganizationResponse.from(organization)));
    }

    private String[] splitName(String source) {
        String trimmed = source.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex < 0) {
            return new String[] { trimmed, trimmed };
        }
        return new String[] { trimmed.substring(0, spaceIndex), trimmed.substring(spaceIndex + 1).trim() };
    }
}
