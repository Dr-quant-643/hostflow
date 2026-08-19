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
    }

    private void seedIfMissing(String code, NotificationChannel channel, String subject, String body) {
        if (repository.findByCode(code).isPresent()) {
            return;
        }
        repository.save(new NotificationTemplate(code, channel, subject, body));
    }
}
