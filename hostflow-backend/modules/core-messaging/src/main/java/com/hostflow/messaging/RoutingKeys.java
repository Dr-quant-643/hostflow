package com.hostflow.messaging;

public final class RoutingKeys {

    private RoutingKeys() {
    }

    public static final String BOOKING_CREATED = "booking.created";
    public static final String BOOKING_CONFIRMED = "booking.confirmed";
    public static final String BOOKING_CANCELLED = "booking.cancelled";
    public static final String BOOKING_EXPIRED = "booking.expired";

    public static final String PROPERTY_CREATED = "property.created";
    public static final String PROPERTY_UPDATED = "property.updated";
    public static final String PROPERTY_DELETED = "property.deleted";

    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String PAYMENT_REFUNDED = "payment.refunded";

    public static final String NOTIFICATION_EMAIL = "notification.email";
    public static final String NOTIFICATION_SMS = "notification.sms";
    public static final String NOTIFICATION_PUSH = "notification.push";
    public static final String NOTIFICATION_WHATSAPP = "notification.whatsapp";

    public static final String TENANT_CREATED = "tenant.created";
    public static final String TENANT_UPDATED = "tenant.updated";

    /** Topic-exchange wildcard keys — publish with a concrete suffix, e.g.
     * "analytics.event.booking_viewed", matching the analytics.event.* pattern. */
    public static final String ANALYTICS_EVENT_PREFIX = "analytics.event.";
    public static final String ANALYTICS_METRIC_PREFIX = "analytics.metric.";
    public static final String AI_GENERATION_PREFIX = "ai.generation.";
    public static final String AI_ANALYSIS_PREFIX = "ai.analysis.";
}
