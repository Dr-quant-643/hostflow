package com.hostflow.app.publicapi;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.service.NotificationService;
import com.hostflow.rental.entity.RentalInquiry;
import com.hostflow.rental.service.RentalInquiryService;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
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
 *
 * The inquiry itself is now persisted (RentalInquiryService, module-rental)
 * BEFORE the notification fires -- previously this was pure fire-and-forget,
 * so an owner had no way to look up "what did this guest actually ask" or
 * reply to them anywhere but their own email inbox.
 */
@Component
public class RentalInquiryOrchestrator {

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final NotificationService notificationService;
    private final RentalInquiryService rentalInquiryService;

    public RentalInquiryOrchestrator(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            NotificationService notificationService, RentalInquiryService rentalInquiryService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.notificationService = notificationService;
        this.rentalInquiryService = rentalInquiryService;
    }

    public void send(UUID guestUserId, RentalInquiryRequest request) {
        Map<String, Object> row = resolveActiveProperty(request.propertyId());
        UUID tenantId = UUID.fromString(row.get("tenant_id").toString());
        UUID ownerUserId = UUID.fromString(row.get("owner_user_id").toString());
        String propertyName = row.get("name").toString();
        String message = request.message() != null ? request.message() : "(no message)";

        String recipientAddress = resolveEmail(ownerUserId);
        if (recipientAddress == null) {
            throw new ResourceNotFoundException("Owner contact for property", request.propertyId());
        }

        TenantContext.set(tenantId);
        try {
            rentalInquiryService.create(request.propertyId(), guestUserId, ownerUserId, message);
            notificationService.send(new SendNotificationRequest(ownerUserId, recipientAddress,
                    "rental_inquiry_owner",
                    Map.of("property_name", propertyName, "message", message)));
        } finally {
            TenantContext.clear();
        }
    }

    public record MyRentalInquiryRow(UUID id, UUID propertyId, String propertyName, String message,
            String status, String replyMessage) {
    }

    /**
     * Cross-tenant by necessity -- same reasoning as GuestBookingOrchestrator's
     * myBookings(): the guest has no tenant of their own to scope an RLS-backed
     * JPA query by, so this reads directly via platformAdminJdbcTemplate rather
     * than through RentalInquiryService (which is tenant-scoped, correctly, for
     * the owner-facing side).
     */
    public List<MyRentalInquiryRow> myInquiries(UUID guestUserId) {
        String sql = """
                SELECT i.id, i.property_id, p.name AS property_name, i.message, i.status, i.reply_message
                FROM rental_inquiries i
                JOIN properties p ON p.id = i.property_id
                WHERE i.guest_user_id = ?
                ORDER BY i.created_at DESC
                """;
        return platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> new MyRentalInquiryRow(
                UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                rs.getString("property_name"), rs.getString("message"), rs.getString("status"),
                rs.getString("reply_message")), guestUserId);
    }

    /**
     * Owner-facing list -- standard tenant-scoped read via RentalInquiryService,
     * no cross-tenant lookup needed since the caller's own JWT already resolves
     * their tenant through the normal request-scoped filter.
     */
    public Page<RentalInquiry> listForProperty(UUID propertyId, int limit, int offset) {
        return rentalInquiryService.listByProperty(propertyId, limit, offset);
    }

    /**
     * Replying is the one owner-side action here that also needs the
     * cross-tenant guest-notification machinery this orchestrator already has
     * for send() -- module-rental's RentalInquiryService has no access to
     * platformAdminJdbcTemplate or guest-email resolution, so that stays
     * centralized here rather than duplicated.
     */
    public RentalInquiry reply(UUID inquiryId, String replyMessage) {
        RentalInquiry inquiry = rentalInquiryService.reply(inquiryId, replyMessage);

        String recipientAddress = resolveEmail(inquiry.getGuestUserId());
        if (recipientAddress == null) {
            return inquiry;
        }
        Map<String, Object> row = platformAdminJdbcTemplate.queryForMap(
                "SELECT name FROM properties WHERE id = ?", inquiry.getPropertyId());
        notificationService.send(new SendNotificationRequest(inquiry.getGuestUserId(), recipientAddress,
                "rental_inquiry_reply_guest",
                Map.of("property_name", row.get("name").toString(), "reply_message", replyMessage)));
        return inquiry;
    }

    private Map<String, Object> resolveActiveProperty(UUID propertyId) {
        List<Map<String, Object>> rows = platformAdminJdbcTemplate.queryForList(
                "SELECT tenant_id, owner_user_id, name FROM properties WHERE id = ? AND status = 'ACTIVE'",
                propertyId);
        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        return rows.get(0);
    }

    private String resolveEmail(UUID userId) {
        List<String> userEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        if (!userEmail.isEmpty()) {
            return userEmail.get(0);
        }
        List<String> guestEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM guest_profiles WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        return guestEmail.isEmpty() ? null : guestEmail.get(0);
    }
}
