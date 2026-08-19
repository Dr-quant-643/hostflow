package com.hostflow.notification.messaging;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.messaging.RoutingKeys;
import com.hostflow.messaging.config.HostFlowRabbitProperties;
import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.entity.NotificationLog;
import com.hostflow.notification.repository.NotificationLogRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * UPDATED: now requires the CALLER to supply the resolved recipientAddress
 * (email/phone/device-token) rather than resolving it internally — this keeps
 * module-notification decoupled from module-identity/module-property (no direct
 * dependency needed), matching the independent-module convention. Callers (e.g.
 * NotificationService, or any future module triggering a notification) are
 * responsible for looking up the address from their own known User/GuestProfile
 * record before calling publish().
 */
@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationLogRepository notificationLogRepository;
    private final HostFlowRabbitProperties rabbitProperties;

    public NotificationPublisher(RabbitTemplate rabbitTemplate, NotificationLogRepository notificationLogRepository,
                                  HostFlowRabbitProperties rabbitProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationLogRepository = notificationLogRepository;
        this.rabbitProperties = rabbitProperties;
    }

    @Transactional
    public NotificationLog publish(UUID recipientUserId, String recipientAddress, String templateCode,
                                    NotificationChannel channel, String subject, String renderedBody) {
        if (recipientAddress == null || recipientAddress.isBlank()) {
            throw new BusinessRuleException("Cannot send a " + channel + " notification with no recipient address");
        }

        NotificationLog log = new NotificationLog(recipientUserId, templateCode, channel);
        log = notificationLogRepository.save(log);

        NotificationMessage message = new NotificationMessage(
                log.getId(), TenantContext.require(), recipientUserId, recipientAddress, channel, subject, renderedBody);

        String routingKey = resolveRoutingKey(channel);
        rabbitTemplate.convertAndSend(rabbitProperties.getExchanges().getDirect(), routingKey, message);

        return log;
    }

    private String resolveRoutingKey(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> RoutingKeys.NOTIFICATION_EMAIL;
            case SMS -> RoutingKeys.NOTIFICATION_SMS;
            case PUSH -> RoutingKeys.NOTIFICATION_PUSH;
            case WHATSAPP -> RoutingKeys.NOTIFICATION_WHATSAPP; // see Category 4 fix below — resolved properly there
        };
    }
}
