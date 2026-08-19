package com.hostflow.tenancy.entity;

import com.hostflow.common.exception.TenantContextMissingException;
import com.hostflow.persistence.entity.BaseEntity;
import com.hostflow.tenancy.context.TenantContext;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.UUID;

/**
 * Base class for every entity owned by a tenant (Property, Booking, CrmContact,
 * etc.).
 * Auto-assigns tenant_id from TenantContext at persist time if not already set,
 * so
 * module code never has to remember to set it manually — and cannot forget to.
 *
 * Application-level tenant_id assignment here is a defense-in-depth layer, NOT
 * the
 * primary isolation mechanism — Postgres RLS (enforced via current_tenant_id()
 * and the
 * SET LOCAL wiring in TenantAwareJpaTransactionManager) is the actual security
 * boundary.
 * This column existing and being correct is still required for RLS policies to
 * have
 * something to compare against.
 */
@MappedSuperclass
public abstract class TenantScopedEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @PrePersist
    @PreUpdate
    protected void assignTenantIfMissing() {
        if (tenantId == null) {
            UUID tenant = TenantContext.get();
            if (tenant == null) {
                throw new TenantContextMissingException();
            }
            tenantId = tenant;
        }
    }

    public UUID getTenantId() {
        return tenantId;
    }

    protected void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
}
