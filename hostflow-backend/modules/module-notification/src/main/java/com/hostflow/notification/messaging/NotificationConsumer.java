package com.hostflow.notification.messaging;

import com.hostflow.messaging.QueueNames;
import com.hostflow.notification.delivery.EmailDeliveryService;
import com.hostflow.notification.delivery.PushDeliveryService;
import com.hostflow.notification.delivery.SmsDeliveryService;
import com.hostflow.notification.delivery.WhatsAppDeliveryService;
import com.hostflow.notification.service.NotificationLogUpdateService;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationLogUpdateService notificationLogUpdateService;
    private final EmailDeliveryService emailDeliveryService;
    private final SmsDeliveryService smsDeliveryService;
    private final PushDeliveryService pushDeliveryService;
    private final WhatsAppDeliveryService whatsAppDeliveryService;

    public NotificationConsumer(NotificationLogUpdateService notificationLogUpdateService,
                                 EmailDeliveryService emailDeliveryService,
                                 SmsDeliveryService smsDeliveryService,
                                 PushDeliveryService pushDeliveryService,
                                 WhatsAppDeliveryService whatsAppDeliveryService) {
        this.notificationLogUpdateService = notificationLogUpdateService;
        this.emailDeliveryService = emailDeliveryService;
        this.smsDeliveryService = smsDeliveryService;
        this.pushDeliveryService = pushDeliveryService;
        this.whatsAppDeliveryService = whatsAppDeliveryService;
    }

    // Deliberately NOT @Transactional at the listener level, same reasoning as
    // DomainAuditEventConsumer's non-@Transactional listeners: that would open
    // the transaction (and issue SET LOCAL) before TenantContext.set() below
    // ever runs. Each DB touch instead goes through NotificationLogUpdateService,
    // which opens its own transaction per call, safely after TenantContext is set.
    @RabbitListener(queues = QueueNames.NOTIFICATION_EMAIL)
    public void consumeEmail(NotificationMessage message) {
        process(message, () -> emailDeliveryService.send(message.recipientAddress(), message.subject(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_SMS)
    public void consumeSms(NotificationMessage message) {
        process(message, () -> smsDeliveryService.send(message.recipientAddress(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_PUSH)
    public void consumePush(NotificationMessage message) {
        process(message, () -> pushDeliveryService.send(message.recipientAddress(), message.subject(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_WHATSAPP)
    public void consumeWhatsApp(NotificationMessage message) {
        process(message, () -> whatsAppDeliveryService.send(message.recipientAddress(), message.renderedBody()));
    }

    private void process(NotificationMessage message, Runnable deliveryAction) {
        TenantContext.set(message.tenantId());
        try {
            handle(message, deliveryAction);
        } finally {
            TenantContext.clear();
        }
    }

    private void handle(NotificationMessage message, Runnable deliveryAction) {
        notificationLogUpdateService.assertExists(message.notificationLogId());

        try {
            deliveryAction.run();
            notificationLogUpdateService.markSent(message.notificationLogId());
            log.info("Delivered notification {} via {} to {}",
                    message.notificationLogId(), message.channel(), message.recipientAddress());
        } catch (Exception e) {
            notificationLogUpdateService.markFailed(message.notificationLogId(), e.getMessage());
            log.error("Delivery failed for notification {} via {}: {}",
                    message.notificationLogId(), message.channel(), e.getMessage());
            throw e;
        }
    }
}
