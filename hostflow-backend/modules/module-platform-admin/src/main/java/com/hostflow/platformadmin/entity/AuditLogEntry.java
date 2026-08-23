package com.hostflow.platformadmin.entity;

import com.hostflow.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Not tenant-scoped and deliberately append-only (no update methods) — an audit
 * log that could be edited defeats its own purpose. actorTenantId is stored as a
 * plain column (not enforced via RLS) since PLATFORM_ADMIN needs to query across
 * ALL tenants' audit entries in one view — this is inherently a cross-tenant
 * concept, same category as the platform-admin read queries built earlier.
 */
@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry extends BaseEntity {

    /** Null for system-initiated events with no human actor, e.g. TENANT_CREATED. */
    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "actor_tenant_id")
    private UUID actorTenantId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    protected AuditLogEntry() {
    }

    public AuditLogEntry(UUID actorUserId, UUID actorTenantId, String action, String resourceType,
                          String resourceId, String detail) {
        this.actorUserId = actorUserId;
        this.actorTenantId = actorTenantId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.detail = detail;
    }

    public UUID getActorUserId() {
        return actorUserId;
    }

    public UUID getActorTenantId() {
        return actorTenantId;
    }

    public String getAction() {
        return action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getDetail() {
        return detail;
    }
}
