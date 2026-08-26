package com.hostflow.app.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostflow.identity.entity.WebhookSubscription;
import com.hostflow.identity.repository.WebhookSubscriptionRepository;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
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
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient webhookHttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public DomainAuditEventConsumer(AuditLogService auditLogService,
                                     NotificationService notificationService,
                                     NotificationTemplateSeedService templateSeedService,
                                     @Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
                                     WebhookSubscriptionRepository webhookSubscriptionRepository,
                                     ObjectMapper objectMapper) {
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
        this.templateSeedService = templateSeedService;
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.objectMapper = objectMapper;
    }

    /** Same non-@Transactional reasoning as bookingConfirmed() below. */
    @RabbitListener(queues = QueueNames.BOOKING_CREATED)
    public void bookingCreated(DomainEventMessage event) {
        auditOnly(event);
        attemptNewBookingOwnerNotification(event);
        attemptWebhookDelivery(event, "booking.created");
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
        attemptWebhookDelivery(event, "booking.confirmed");
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

    /**
     * Delivers `event` to every active WebhookSubscription this tenant has
     * for `eventType` -- the foundation half of the public-API/webhooks
     * feature (see PublicApiController/ApiKey doc comments for the "no
     * rate limiting/billing yet" scope note). Best-effort per subscription:
     * one owner's misconfigured endpoint must never affect another's
     * delivery, the audit trail, or crash this consumer.
     */
    private void attemptWebhookDelivery(DomainEventMessage event, String eventType) {
        TenantContext.set(event.tenantId());
        List<WebhookSubscription> subscriptions;
        try {
            subscriptions = webhookSubscriptionRepository.findByEventTypeAndActiveTrue(eventType);
        } catch (Exception e) {
            log.warn("Could not look up webhook subscriptions for {}: {}", eventType, e.getMessage());
            return;
        } finally {
            TenantContext.clear();
        }
        if (subscriptions.isEmpty()) {
            return;
        }

        String payload;
        try {
            payload = objectMapper.writeValueAsString(Map.of(
                    "event", eventType,
                    "resourceType", event.resourceType(),
                    "resourceId", event.resourceId().toString(),
                    "detail", event.detail()));
        } catch (Exception e) {
            log.warn("Could not serialize webhook payload for {}: {}", eventType, e.getMessage());
            return;
        }

        for (WebhookSubscription subscription : subscriptions) {
            try {
                String signature = hmacSha256(subscription.getSecret(), payload);
                HttpRequest request = HttpRequest.newBuilder(URI.create(subscription.getUrl()))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .header("X-RvanaFlow-Signature", signature)
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();
                webhookHttpClient.send(request, HttpResponse.BodyHandlers.discarding());
            } catch (Exception e) {
                log.warn("Webhook delivery failed for subscription {}: {}", subscription.getId(), e.getMessage());
            }
        }
    }

    private String hmacSha256(String secret, String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
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
