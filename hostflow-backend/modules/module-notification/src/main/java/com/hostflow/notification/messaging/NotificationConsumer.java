package com.hostflow.notification.messaging;

import com.hostflow.messaging.QueueNames;
import com.hostflow.notification.delivery.EmailDeliveryService;
import com.hostflow.notification.delivery.PushDeliveryService;
import com.hostflow.notification.delivery.SmsDeliveryService;
import com.hostflow.notification.delivery.WhatsAppDeliveryService;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.repository.NotificationLogRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationLogRepository notificationLogRepository;
    private final EmailDeliveryService emailDeliveryService;
    private final SmsDeliveryService smsDeliveryService;
    private final PushDeliveryService pushDeliveryService;
    private final WhatsAppDeliveryService whatsAppDeliveryService;

    public NotificationConsumer(NotificationLogRepository notificationLogRepository,
                                 EmailDeliveryService emailDeliveryService,
                                 SmsDeliveryService smsDeliveryService,
                                 PushDeliveryService pushDeliveryService,
                                 WhatsAppDeliveryService whatsAppDeliveryService) {
        this.notificationLogRepository = notificationLogRepository;
        this.emailDeliveryService = emailDeliveryService;
        this.smsDeliveryService = smsDeliveryService;
        this.pushDeliveryService = pushDeliveryService;
        this.whatsAppDeliveryService = whatsAppDeliveryService;
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_EMAIL)
    @Transactional
    public void consumeEmail(NotificationMessage message) {
        process(message, () -> emailDeliveryService.send(message.recipientAddress(), message.subject(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_SMS)
    @Transactional
    public void consumeSms(NotificationMessage message) {
        process(message, () -> smsDeliveryService.send(message.recipientAddress(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_PUSH)
    @Transactional
    public void consumePush(NotificationMessage message) {
        process(message, () -> pushDeliveryService.send(message.recipientAddress(), message.subject(), message.renderedBody()));
    }

    @RabbitListener(queues = QueueNames.NOTIFICATION_WHATSAPP)
    @Transactional
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
        NotificationLog notificationLog = notificationLogRepository.findById(message.notificationLogId())
                .orElseThrow(() -> new IllegalStateException(
                        "NotificationLog " + message.notificationLogId() + " not found for queued message"));

        try {
            deliveryAction.run();
            notificationLog.markSent();
            log.info("Delivered notification {} via {} to {}",
                    notificationLog.getId(), message.channel(), message.recipientAddress());
        } catch (Exception e) {
            notificationLog.markFailed(e.getMessage());
            log.error("Delivery failed for notification {} via {}: {}",
                    notificationLog.getId(), message.channel(), e.getMessage());
            throw e;
        }
    }
}
