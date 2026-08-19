package com.hostflow.notification.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * body uses {{variable_name}} placeholders, resolved by NotificationService.render()
 * against the variables map supplied at send time. Kept as simple string
 * substitution rather than a full templating engine (Thymeleaf/FreeMarker) — Phase 1/2
 * notification bodies (booking confirmations, welcome emails) don't need loops or
 * conditionals; revisit if template complexity grows.
 */
@Entity
@Table(name = "notification_templates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"}))
public class NotificationTemplate extends TenantScopedEntity {

    @Column(name = "code", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    protected NotificationTemplate() {
    }

    public NotificationTemplate(String code, NotificationChannel channel, String subject, String body) {
        this.code = code;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
