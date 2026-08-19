package com.hostflow.notification.messaging;

/**
 * DEPRECATED: this class previously declared module-notification's own
 * exchange/queue/DLQ (hostflow.notifications.exchange/.queue/.dlx/.dlq),
 * disconnected from the real hostflow.rabbitmq.* config. That topology has been
 * replaced by core-messaging's HostFlowRabbitTopologyConfig, which declares the
 * ACTUAL configured queues (hostflow.notification.email/.sms/.push) bound to
 * the
 * shared hostflow.direct exchange and shared hostflow.dlq. This class is
 * intentionally left as an empty marker (not deleted outright) so its git
 * history
 * and the reasoning for the change stay visible in the codebase. Safe to delete
 * entirely in a later cleanup pass once the migration is confirmed stable.
 */
public final class RabbitMQConfig {

    private RabbitMQConfig() {
    }
}
