package com.hostflow.mall.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "mall_retail_units")
public class RetailUnit extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "unit_number", nullable = false)
    private String unitNumber;

    @Column(name = "size_sqm")
    private BigDecimal sizeSqm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RetailUnitStatus status;

    protected RetailUnit() {
    }

    public RetailUnit(UUID propertyId, String unitNumber, BigDecimal sizeSqm) {
        this.propertyId = propertyId;
        this.unitNumber = unitNumber;
        this.sizeSqm = sizeSqm;
        this.status = RetailUnitStatus.VACANT;
    }

    public void markOccupied() {
        if (status != RetailUnitStatus.VACANT) {
            throw new BusinessRuleException("Cannot occupy a unit with status " + status + " (expected VACANT)");
        }
        this.status = RetailUnitStatus.OCCUPIED;
    }

    public void markVacant() {
        this.status = RetailUnitStatus.VACANT;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public BigDecimal getSizeSqm() {
        return sizeSqm;
    }

    public RetailUnitStatus getStatus() {
        return status;
    }
}
