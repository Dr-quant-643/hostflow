package com.hostflow.notification.service;

import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Extracted out of NotificationConsumer specifically so each DB touch gets
 * its own transaction opened AFTER TenantContext.set() has already run —
 * same reasoning documented on DomainAuditEventConsumer's non-@Transactional
 * listener methods. NotificationConsumer's listener methods used to carry
 * @Transactional directly, which opens the transaction (and issues SET LOCAL)
 * before the message body is even deserialized, let alone before
 * TenantContext.set(message.tenantId()) runs -- current_tenant_id() was NULL
 * for the whole method, so RLS correctly-but-uselessly hid every row,
 * surfacing as "NotificationLog ... not found" even though it existed.
 * A private @Transactional method on NotificationConsumer itself wouldn't
 * have worked either -- Spring's proxy-based AOP doesn't intercept
 * self-invocation, so it needs to be a separate bean.
 */
@Service
public class NotificationLogUpdateService {

    private final NotificationLogRepository repository;

    public NotificationLogUpdateService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public void assertExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalStateException("NotificationLog " + id + " not found for queued message");
        }
    }

    @Transactional
    public void markSent(UUID id) {
        repository.findById(id).ifPresent(NotificationLog::markSent);
    }

    @Transactional
    public void markFailed(UUID id, String reason) {
        repository.findById(id).ifPresent(log -> log.markFailed(reason));
    }
}
