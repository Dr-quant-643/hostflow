package com.hostflow.identity.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * An owner-configured URL to POST event payloads to (booking.created,
 * booking.confirmed, ...) -- delivery itself is wired into
 * DomainAuditEventConsumer (app module), which already listens to these
 * domain events for audit logging; this just adds a second thing that
 * listener does per event. secret signs each delivery (HMAC-SHA256, header
 * X-RvanaFlow-Signature) so the receiving endpoint can verify authenticity,
 * same generation approach as ApiKey's raw key.
 */
@Entity
@Table(name = "webhook_subscriptions")
public class WebhookSubscription extends TenantScopedEntity {

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "secret", nullable = false)
    private String secret;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected WebhookSubscription() {
    }

    public WebhookSubscription(String url, String eventType, String secret) {
        this.url = url;
        this.eventType = eventType;
        this.secret = secret;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getUrl() {
        return url;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isActive() {
        return active;
    }
}
