package com.hostflow.messaging;

import java.io.Serializable;
import java.util.UUID;

/**
 * ONE shared event shape used by every domain publisher (booking/property/
 * payment/tenant) — avoids a bespoke message record per module. tenantId is
 * pulled directly from the source entity's own getTenantId() (TenantScopedEntity)
 * by each publisher, NOT from TenantContext — safer, since it doesn't depend on
 * TenantContext still being set at publish time.
 */
public record DomainEventMessage(
        UUID tenantId,
        UUID actorUserId,
        String resourceType,
        UUID resourceId,
        String action,
        String detail
) implements Serializable {
}
