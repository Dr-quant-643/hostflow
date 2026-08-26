package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Lives in app/publicapi (not module-analytics) because it needs the same
 * cross-module, cross-tenant platformAdminJdbcTemplate access as
 * OwnerWorkOrderQueries/RentalInquiryOrchestrator -- module-analytics's own
 * AnalyticsService is deliberately tenant-scoped-only (see its own doc
 * comment), and this reads across bookings + leases + rental_tenants +
 * properties + guest identity tables at once.
 */
@RestController
@RequestMapping("/api/v1/analytics/guest-segments")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class GuestSegmentController {

    private final GuestSegmentQueries queries;

    public GuestSegmentController(GuestSegmentQueries queries) {
        this.queries = queries;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GuestSegmentQueries.GuestSegmentRow>>> mine(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(
                queries.segmentsForOwner(UUID.fromString(jwt.getSubject()))));
    }
}
