package com.hostflow.messaging;

/**
 * Mirrors hostflow.rabbitmq.queues.* exactly (application-dev.yml). Kept as plain
 * string constants (not read from HostFlowRabbitProperties at @RabbitListener
 * annotation time, since annotation attributes must be compile-time constants,
 * not injected beans) — if these ever drift from the YAML, HostFlowRabbitTopologyConfig
 * would still declare the YAML's queues correctly, but a listener referencing a
 * stale constant here would listen to a queue nothing publishes to. Keep these in
 * sync with application-dev.yml manually; this is a known sharp edge, documented
 * here explicitly.
 */
public final class QueueNames {

    private QueueNames() {
    }

    public static final String BOOKING_CREATED = "hostflow.booking.created";
    public static final String BOOKING_CONFIRMED = "hostflow.booking.confirmed";
    public static final String BOOKING_CANCELLED = "hostflow.booking.cancelled";
    public static final String BOOKING_EXPIRED = "hostflow.booking.expired";

    public static final String PROPERTY_CREATED = "hostflow.property.created";
    public static final String PROPERTY_UPDATED = "hostflow.property.updated";
    public static final String PROPERTY_DELETED = "hostflow.property.deleted";

    public static final String PAYMENT_SUCCESS = "hostflow.payment.success";
    public static final String PAYMENT_FAILED = "hostflow.payment.failed";
    public static final String PAYMENT_REFUNDED = "hostflow.payment.refunded";

    public static final String NOTIFICATION_EMAIL = "hostflow.notification.email";
    public static final String NOTIFICATION_SMS = "hostflow.notification.sms";
    public static final String NOTIFICATION_PUSH = "hostflow.notification.push";
    public static final String NOTIFICATION_WHATSAPP= "hostflow.notification.whatsapp";

    public static final String ANALYTICS_EVENT = "hostflow.analytics.event";
    public static final String ANALYTICS_METRIC = "hostflow.analytics.metric";

    public static final String TENANT_CREATED = "hostflow.tenant.created";
    public static final String TENANT_UPDATED = "hostflow.tenant.updated";

    public static final String AI_GENERATION = "hostflow.ai.generation";
    public static final String AI_ANALYSIS = "hostflow.ai.analysis";
}
