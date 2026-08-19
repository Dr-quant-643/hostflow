package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class GuestDashboardController {

    private final GuestDashboardService dashboardService;

    public GuestDashboardController(GuestDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/v1/dashboard/mine")
    public ResponseEntity<ApiResponse<GuestDashboardService.GuestDashboardResponse>> myDashboard(
            @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(dashboardService.buildDashboard(guestUserId)));
    }
}
