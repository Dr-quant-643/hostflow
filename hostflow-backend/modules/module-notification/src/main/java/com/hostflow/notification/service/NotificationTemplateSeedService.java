package com.hostflow.notification.service;

import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.entity.NotificationTemplate;
import com.hostflow.notification.repository.NotificationTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Closes the "no standard templates auto-created on org onboarding" gap flagged
 * in PROJECT_STATE.md — without this, DomainAuditEventConsumer's best-effort
 * booking-confirmed email silently no-ops for every new org. Callers must set
 * TenantContext to the target org BEFORE invoking seedDefaults() (same
 * convention as RentalTenantService.linkToUser) — RLS then scopes both the
 * existence check and the insert to that tenant.
 */
@Service
public class NotificationTemplateSeedService {

    private final NotificationTemplateRepository repository;

    public NotificationTemplateSeedService(NotificationTemplateRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void seedDefaults() {
        seedIfMissing("booking_confirmed", NotificationChannel.EMAIL, "Your booking is confirmed",
                "Hi, your booking is confirmed for check-in {{check_in}} and check-out {{check_out}}. "
                        + "We look forward to hosting you.");
        seedIfMissing("new_booking_owner", NotificationChannel.EMAIL, "New booking on {{property_name}}",
                "You have a new booking on {{property_name}} for check-in {{check_in}} and check-out {{check_out}}.");
        seedIfMissing("rental_inquiry_owner", NotificationChannel.EMAIL, "New rental inquiry on {{property_name}}",
                "A prospective tenant is interested in renting {{property_name}}. Message: {{message}}");
        seedIfMissing("rental_inquiry_reply_guest", NotificationChannel.EMAIL, "The owner replied about {{property_name}}",
                "The owner of {{property_name}} replied to your inquiry: {{reply_message}}");
    }

    private void seedIfMissing(String code, NotificationChannel channel, String subject, String body) {
        if (repository.findByCode(code).isPresent()) {
            return;
        }
        repository.save(new NotificationTemplate(code, channel, subject, body));
    }
}
