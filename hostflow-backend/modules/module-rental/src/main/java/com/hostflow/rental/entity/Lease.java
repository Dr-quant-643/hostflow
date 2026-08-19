package com.hostflow.rental.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "leases")
public class Lease extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "tenant_id_ref", nullable = false)
    private UUID tenantIdRef; // RentalTenant.id — named to avoid confusion with the multi-tenancy tenant_id column

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "monthly_rent", precision = 12, scale = 2, nullable = false)
    private BigDecimal monthlyRent;

    @Column(name = "security_deposit", precision = 12, scale = 2)
    private BigDecimal securityDeposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaseStatus status;

    protected Lease() {
    }

    public Lease(UUID propertyId, UUID tenantIdRef, LocalDate startDate, LocalDate endDate,
                 BigDecimal monthlyRent, BigDecimal securityDeposit) {
        if (!endDate.isAfter(startDate)) {
            throw new BusinessRuleException("Lease end date must be after start date");
        }
        if (monthlyRent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Monthly rent must be greater than zero");
        }
        this.propertyId = propertyId;
        this.tenantIdRef = tenantIdRef;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.securityDeposit = securityDeposit;
        this.status = LeaseStatus.DRAFT;
    }

    public void activate() {
        if (status != LeaseStatus.DRAFT) {
            throw new BusinessRuleException("Cannot activate a lease with status " + status + " (expected DRAFT)");
        }
        this.status = LeaseStatus.ACTIVE;
    }

    public void terminate() {
        if (status != LeaseStatus.ACTIVE) {
            throw new BusinessRuleException("Cannot terminate a lease with status " + status + " (expected ACTIVE)");
        }
        this.status = LeaseStatus.TERMINATED;
    }

    public void expire() {
        if (status != LeaseStatus.ACTIVE) {
            throw new BusinessRuleException("Cannot expire a lease with status " + status + " (expected ACTIVE)");
        }
        this.status = LeaseStatus.EXPIRED;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getTenantIdRef() {
        return tenantIdRef;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public LeaseStatus getStatus() {
        return status;
    }
}
