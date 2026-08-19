package com.hostflow.app.messaging;

/**
 * DEPRECATED: previously defined two named container factories
 * (notificationListenerContainerFactory,
 * contentGenerationListenerContainerFactory)
 * with differing retry policies — this was open item B (never actually wired to
 * the @RabbitListener annotations). Now moot: your real application-dev.yml
 * already configures retry via spring.rabbitmq.listener.simple.retry.*
 * (max-attempts:
 * 3, initial-interval: 1000ms, multiplier: 2.0, max-interval: 10000ms), which
 * Spring Boot applies automatically to the DEFAULT listener container factory
 * that ALL @RabbitListener methods use when no explicit containerFactory is
 * specified. This is simpler and now correctly closes open item B — every
 * consumer (NotificationConsumer's 3 methods, ContentGenerationConsumer's 1
 * method) gets the same retry policy from the real YAML, with no per-queue
 * differentiation. If different domains genuinely need different retry policies
 * later (e.g. AI generation needing longer backoff than notifications), that's
 * a
 * real future enhancement, not silently lost — flagged in the module report.
 */
public final class RabbitListenerContainerConfig {

    private RabbitListenerContainerConfig() {
    }
}
