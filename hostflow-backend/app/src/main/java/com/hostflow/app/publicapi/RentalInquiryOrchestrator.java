package com.hostflow.app.publicapi;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.service.NotificationService;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A MONTHLY-classified property has no self-service booking flow (see
 * RentalModel's javadoc) -- a guest interested in renting sends an inquiry,
 * the owner gets notified, and follows up to vet the prospective tenant and
 * create the formal Lease through the existing staff-facing LeaseController.
 * This is intentionally NOT wired to auto-create a Lease -- same trust
 * boundary as leasing today, just adding the missing "guest can express
 * interest" step. Mirrors GuestBookingOrchestrator's cross-tenant property
 * lookup and TenantContext discipline exactly.
 *
 * UNLIKE the best-effort notification call sites elsewhere in this codebase
 * (e.g. DomainAuditEventConsumer's booking-confirmed email), this does NOT
 * swallow notification failures -- there, the notification is a side effect
 * of an already-succeeded primary write (the booking/audit record); here, the
 * notification IS the entire point of the request. Swallowing a failure would
 * return a false-positive 200 to a guest whose inquiry the owner never saw.
 */
@Component
public class RentalInquiryOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final NotificationService notificationService;

    public RentalInquiryOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            NotificationService notificationService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.notificationService = notificationService;
    }

    public void send(RentalInquiryRequest request) {
        List<Map<String, Object>> rows = platformAdminJdbcTemplate.queryForList(
                "SELECT tenant_id, owner_user_id, name FROM properties WHERE id = ? AND status = 'ACTIVE'",
                request.propertyId());
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Property", request.propertyId());
        }
        Map<String, Object> row = rows.get(0);
        UUID tenantId = UUID.fromString(row.get("tenant_id").toString());
        UUID ownerUserId = UUID.fromString(row.get("owner_user_id").toString());
        String propertyName = row.get("name").toString();

        String recipientAddress = resolveOwnerEmail(ownerUserId);
        if (recipientAddress == null) {
            throw new ResourceNotFoundException("Owner contact for property", request.propertyId());
        }

        TenantContext.set(tenantId);
        try {
            notificationService.send(new SendNotificationRequest(ownerUserId, recipientAddress,
                    "rental_inquiry_owner",
                    Map.of("property_name", propertyName,
                            "message", request.message() != null ? request.message() : "(no message)")));
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveOwnerEmail(UUID userId) {
        List<String> userEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        return userEmail.isEmpty() ? null : userEmail.get(0);
    }
}
