package com.hostflow.notification.entity;

/**
 * QUEUED is set by NotificationPublisher at publish time (before RabbitMQ delivery
 * is even confirmed). SENT/FAILED are set by NotificationConsumer after attempting
 * actual delivery. DEAD_LETTERED is set when a message exhausts its retry count and
 * lands in the dead-letter queue (wired in RabbitMQConfig).
 */
public enum NotificationStatus {
    QUEUED,
    SENT,
    FAILED,
    DEAD_LETTERED
}
