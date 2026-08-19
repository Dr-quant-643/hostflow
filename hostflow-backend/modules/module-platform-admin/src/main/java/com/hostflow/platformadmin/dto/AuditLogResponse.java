package com.hostflow.platformadmin.dto;

import com.hostflow.platformadmin.entity.AuditLogEntry;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(UUID id, UUID actorUserId, UUID actorTenantId, String action,
                                String resourceType, String resourceId, String detail, Instant createdAt) {
    public static AuditLogResponse from(AuditLogEntry e) {
        return new AuditLogResponse(e.getId(), e.getActorUserId(), e.getActorTenantId(), e.getAction(),
                e.getResourceType(), e.getResourceId(), e.getDetail(), e.getCreatedAt());
    }
}
