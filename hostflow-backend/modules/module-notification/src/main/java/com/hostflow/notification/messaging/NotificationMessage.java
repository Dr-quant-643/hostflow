package com.hostflow.notification.messaging;

import com.hostflow.notification.entity.NotificationChannel;

import java.io.Serializable;
import java.util.UUID;

/**
 * UPDATED: added recipientAddress — the actual email/phone/device-token to
 * deliver to, resolved by NotificationPublisher BEFORE publishing (since it needs
 * DB access to look up the recipient's contact info, and the consumer running on
 * a different thread has no TenantContext to do that lookup itself). This is what
 * makes real delivery (EmailDeliveryService, etc.) actually possible — the old
 * version only carried a UUID, which is meaningless to an SMTP/SMS provider.
 */
public record NotificationMessage(
        UUID notificationLogId,
        UUID tenantId,
        UUID recipientUserId,
        String recipientAddress,
        NotificationChannel channel,
        String subject,
        String renderedBody
) implements Serializable {
}
