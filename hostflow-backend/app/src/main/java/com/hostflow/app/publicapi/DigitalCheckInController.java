package com.hostflow.app.publicapi;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.common.response.ApiResponse;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings/public/{bookingId}/digital-checkin")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class DigitalCheckInController {

    private final DigitalCheckInService checkInService;
    private final JdbcTemplate platformAdminJdbcTemplate;

    public DigitalCheckInController(DigitalCheckInService checkInService,
                                     @Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.checkInService = checkInService;
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UUID>> confirm(@PathVariable UUID bookingId,
                                                       @RequestParam("idDocument") MultipartFile idDocument,
                                                       @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        UUID tenantId = verifyOwnershipAndResolveTenant(bookingId, guestUserId);

        TenantContext.set(tenantId);
        try {
            var checkIn = checkInService.confirm(bookingId, idDocument);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(checkIn.getId()));
        } finally {
            TenantContext.clear();
        }
    }

    private UUID verifyOwnershipAndResolveTenant(UUID bookingId, UUID guestUserId) {
        List<Object[]> results = platformAdminJdbcTemplate.query(
                "SELECT tenant_id, guest_user_id FROM bookings WHERE id = ?",
                (rs, rowNum) -> new Object[]{rs.getString("tenant_id"), rs.getString("guest_user_id")}, bookingId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Booking", bookingId);
        }
        Object[] row = results.get(0);
        if (!row[1].equals(guestUserId.toString())) {
            throw new BusinessRuleException("This booking does not belong to the current user");
        }
        return UUID.fromString((String) row[0]);
    }
}
