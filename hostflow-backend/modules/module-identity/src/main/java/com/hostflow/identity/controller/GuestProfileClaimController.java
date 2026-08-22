package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.GuestProfileResponse;
import com.hostflow.identity.entity.GuestProfile;
import com.hostflow.identity.service.GuestRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * For a Keycloak identity that already exists (typically via "Continue with
 * Google") but has no product_scope yet -- the NazilCo counterpart to
 * HostWorkspaceClaimController. Only requires isAuthenticated(), not
 * PRODUCT_NAZILCO, since lacking that authority is exactly the situation this
 * endpoint exists to fix. No request body needed (unlike claim-workspace,
 * a guest profile has no name to supply). The caller's session cookie still
 * carries the OLD claims after this succeeds -- the frontend must refresh
 * the token afterward for PRODUCT_NAZILCO to actually take effect.
 */
@RestController
public class GuestProfileClaimController {

    private final GuestRegistrationService registrationService;

    public GuestProfileClaimController(GuestRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/api/v1/guests/claim-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GuestProfileResponse>> claim(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String fullName = jwt.getClaimAsString("name");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        if (firstName == null || lastName == null) {
            String[] parts = splitName(fullName != null ? fullName : email);
            firstName = firstName != null ? firstName : parts[0];
            lastName = lastName != null ? lastName : parts[1];
        }

        GuestProfile profile = registrationService.claimProfile(jwt.getSubject(), email, firstName, lastName);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(GuestProfileResponse.from(profile)));
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
