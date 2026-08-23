package com.hostflow.app.messaging;

import com.hostflow.messaging.DomainEventMessage;
import com.hostflow.messaging.QueueNames;
import com.hostflow.notification.dto.SendNotificationRequest;
import com.hostflow.notification.service.NotificationService;
import com.hostflow.notification.service.NotificationTemplateSeedService;
import com.hostflow.platformadmin.service.AuditLogService;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * THE fix for the "AuditLogService has zero callers" gap flagged in the audit,
 * AND the first real cross-module workflow link (Category 3): one generic
 * listener per domain queue, all writing to the audit log. For BOOKING_CONFIRMED
 * specifically, also attempts a best-effort guest confirmation email — if the
 * owning org has no "booking_confirmed" NotificationTemplate, or the recipient's
 * address can't be resolved, this is caught and logged, NEVER allowed to fail the
 * audit write or crash the listener (a missing notification template is a content
 * gap, not a reason to lose the audit trail). Also seeds default notification
 * templates on TENANT_CREATED — see attemptTemplateSeeding().
 */
@Component
public class DomainAuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DomainAuditEventConsumer.class);

    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final NotificationTemplateSeedService templateSeedService;
    private final JdbcTemplate platformAdminJdbcTemplate;

    public DomainAuditEventConsumer(AuditLogService auditLogService,
                                     NotificationService notificationService,
                                     NotificationTemplateSeedService templateSeedService,
                                     @Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
        this.templateSeedService = templateSeedService;
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    /** Same non-@Transactional reasoning as bookingConfirmed() below. */
    @RabbitListener(queues = QueueNames.BOOKING_CREATED)
    public void bookingCreated(DomainEventMessage event) {
        auditOnly(event);
        attemptNewBookingOwnerNotification(event);
    }

    /**
     * Deliberately NOT @Transactional at this level: attemptBookingConfirmedNotification
     * sets TenantContext itself before calling into notificationService, and
     * TenantAwareJpaTransactionManager reads TenantContext at transaction START (in
     * doBegin) — wrapping this whole method in one ambient transaction would open it
     * before TenantContext is set, so the SET LOCAL would run with no tenant and
     * every write below would be silently RLS-denied. auditLogService.record() and
     * notificationService's repository calls each carry their own @Transactional
     * (or Spring Data's default), so they still get a correctly-scoped transaction.
     */
    @RabbitListener(queues = QueueNames.BOOKING_CONFIRMED)
    public void bookingConfirmed(DomainEventMessage event) {
        auditOnly(event);
        attemptBookingConfirmedNotification(event);
    }

    @RabbitListener(queues = QueueNames.BOOKING_CANCELLED)
    @Transactional
    public void bookingCancelled(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.BOOKING_EXPIRED)
    @Transactional
    public void bookingExpired(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PROPERTY_CREATED)
    @Transactional
    public void propertyCreated(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PROPERTY_UPDATED)
    @Transactional
    public void propertyUpdated(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PROPERTY_DELETED)
    @Transactional
    public void propertyDeleted(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PAYMENT_SUCCESS)
    @Transactional
    public void paymentSucceeded(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PAYMENT_FAILED)
    @Transactional
    public void paymentFailed(DomainEventMessage event) { auditOnly(event); }

    @RabbitListener(queues = QueueNames.PAYMENT_REFUNDED)
    @Transactional
    public void paymentRefunded(DomainEventMessage event) { auditOnly(event); }

    /** Same non-@Transactional reasoning as bookingConfirmed() above. */
    @RabbitListener(queues = QueueNames.TENANT_CREATED)
    public void tenantCreated(DomainEventMessage event) {
        auditOnly(event);
        attemptTemplateSeeding(event);
    }

    @RabbitListener(queues = QueueNames.TENANT_UPDATED)
    @Transactional
    public void tenantUpdated(DomainEventMessage event) { auditOnly(event); }

    private void auditOnly(DomainEventMessage event) {
        auditLogService.record(event.actorUserId(), event.tenantId(), event.action(),
                event.resourceType(), event.resourceId().toString(), event.detail());
    }

    private void attemptBookingConfirmedNotification(DomainEventMessage event) {
        try {
            UUID bookingId = event.resourceId();
            Map<String, Object> row = platformAdminJdbcTemplate.queryForMap(
                    "SELECT guest_user_id, property_id, check_in, check_out FROM bookings WHERE id = ?", bookingId);
            UUID guestUserId = UUID.fromString(row.get("guest_user_id").toString());

            String recipientAddress = resolveGuestEmail(guestUserId);
            if (recipientAddress == null) {
                log.warn("No email found for guest {}, skipping booking-confirmed notification", guestUserId);
                return;
            }

            TenantContext.set(event.tenantId());
            try {
                notificationService.send(new SendNotificationRequest(guestUserId, recipientAddress, "booking_confirmed",
                        Map.of("check_in", row.get("check_in").toString(), "check_out", row.get("check_out").toString())));
            } finally {
                TenantContext.clear();
            }
        } catch (Exception e) {
            // Best-effort only — a missing template or email lookup failure must
            // never break the audit trail or crash this consumer.
            log.warn("Could not send booking-confirmed notification: {}", e.getMessage());
        }
    }

    /**
     * The property OWNER's counterpart to attemptBookingConfirmedNotification —
     * fires as soon as a guest places a booking (not on confirmation), since an
     * owner needs to know about a new booking to prepare regardless of whether
     * the guest has completed checkout yet. Same best-effort, never-crash-the-
     * consumer contract as the guest notification.
     */
    private void attemptNewBookingOwnerNotification(DomainEventMessage event) {
        try {
            UUID bookingId = event.resourceId();
            Map<String, Object> row = platformAdminJdbcTemplate.queryForMap(
                    "SELECT p.owner_user_id, p.name AS property_name, b.check_in, b.check_out " +
                            "FROM bookings b JOIN properties p ON p.id = b.property_id WHERE b.id = ?",
                    bookingId);
            UUID ownerUserId = UUID.fromString(row.get("owner_user_id").toString());

            String recipientAddress = resolveGuestEmail(ownerUserId);
            if (recipientAddress == null) {
                log.warn("No email found for property owner {}, skipping new-booking notification", ownerUserId);
                return;
            }

            TenantContext.set(event.tenantId());
            try {
                notificationService.send(new SendNotificationRequest(ownerUserId, recipientAddress, "new_booking_owner",
                        Map.of(
                                "property_name", row.get("property_name").toString(),
                                "check_in", row.get("check_in").toString(),
                                "check_out", row.get("check_out").toString())));
            } finally {
                TenantContext.clear();
            }
        } catch (Exception e) {
            // Best-effort only — a missing template or email lookup failure must
            // never break the audit trail or crash this consumer.
            log.warn("Could not send new-booking owner notification: {}", e.getMessage());
        }
    }

    private void attemptTemplateSeeding(DomainEventMessage event) {
        TenantContext.set(event.tenantId());
        try {
            templateSeedService.seedDefaults();
        } catch (Exception e) {
            // Best-effort — a seeding failure must never break the audit trail or
            // crash this consumer; org onboarding already succeeded by this point.
            log.warn("Could not seed default notification templates for tenant {}: {}",
                    event.tenantId(), e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * FIXED: this previously queried guest_profiles.id / users.id — both are
     * internally auto-generated primary keys, NOT the Keycloak subject id that
     * guestUserId actually holds here (every guest-facing table in this codebase,
     * e.g. bookings.guest_user_id, stores the raw Keycloak sub, bypassing the
     * internal PK entirely). Matching on keycloak_id is the correct lookup.
     */
    private String resolveGuestEmail(UUID userId) {
        List<String> guestEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM guest_profiles WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        if (!guestEmail.isEmpty()) {
            return guestEmail.get(0);
        }
        List<String> userEmail = platformAdminJdbcTemplate.query(
                "SELECT email FROM users WHERE keycloak_id = ?",
                (rs, rowNum) -> rs.getString("email"), userId.toString());
        return userEmail.isEmpty() ? null : userEmail.get(0);
    }
}
