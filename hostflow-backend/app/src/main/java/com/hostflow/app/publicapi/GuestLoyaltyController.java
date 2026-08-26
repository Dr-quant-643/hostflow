package com.hostflow.app.publicapi;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Closes the loop on segmentation from the OTHER side: GuestSegmentQueries
 * is how an owner sees their guests; this is how a guest sees their own
 * standing with a specific owner (a guest's loyalty status is inherently
 * per-owner, not global -- being a VIP at one host says nothing about a
 * property they've never booked, consistent with segmentation never
 * building a cross-tenant guest profile). Reuses segmentsForOwner rather
 * than adding a new query -- this just resolves which owner and which row
 * belongs to the calling guest.
 */
@RestController
@RequestMapping("/api/v1/analytics/my-loyalty-status")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class GuestLoyaltyController {

    private final GuestSegmentQueries guestSegmentQueries;
    private final JdbcTemplate platformAdminJdbcTemplate;

    public GuestLoyaltyController(GuestSegmentQueries guestSegmentQueries,
            @Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.guestSegmentQueries = guestSegmentQueries;
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record LoyaltyStatus(String segment, int totalStays) {
    }

    @GetMapping
    public ResponseEntity<ApiResponse<LoyaltyStatus>> myStatus(@RequestParam UUID propertyId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        UUID ownerUserId = resolveOwner(propertyId);

        List<GuestSegmentQueries.GuestSegmentRow> segments = guestSegmentQueries.segmentsForOwner(ownerUserId);
        GuestSegmentQueries.GuestSegmentRow mine = segments.stream()
                .filter(row -> row.guestUserId().equals(guestUserId))
                .findFirst()
                .orElse(null);

        if (mine == null) {
            return ResponseEntity.ok(ApiResponse.success(new LoyaltyStatus("NEW", 0)));
        }
        return ResponseEntity.ok(ApiResponse.success(
                new LoyaltyStatus(mine.segment(), mine.totalBookings() + mine.totalReservations())));
    }

    private UUID resolveOwner(UUID propertyId) {
        List<UUID> rows = platformAdminJdbcTemplate.query(
                "SELECT owner_user_id FROM properties WHERE id = ?",
                (rs, rowNum) -> UUID.fromString(rs.getString("owner_user_id")), propertyId);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        return rows.get(0);
    }
}
