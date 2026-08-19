package com.hostflow.billing.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One budget per (property or org-wide) + category + month — enforced at the DB
 * level via a unique constraint (V15 migration), not in application code, so a
 * race between two concurrent budget-set requests can't create duplicates.
 */
@Entity
@Table(name = "budgets")
public class Budget extends TenantScopedEntity {

    @Column(name = "property_id")
    private UUID propertyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "budget_month", nullable = false)
    private java.time.LocalDate budgetMonth;

    @Column(name = "allocated_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal allocatedAmount;

    protected Budget() {
    }

    public Budget(UUID propertyId, ExpenseCategory category, java.time.LocalDate budgetMonth, BigDecimal allocatedAmount) {
        this.propertyId = propertyId;
        this.category = category;
        this.budgetMonth = budgetMonth.withDayOfMonth(1);
        this.allocatedAmount = allocatedAmount;
    }

    public void updateAllocation(BigDecimal newAmount) {
        this.allocatedAmount = newAmount;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public java.time.LocalDate getBudgetMonth() {
        return budgetMonth;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }
}
