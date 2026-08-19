package com.hostflow.billing.entity;

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

/**
 * propertyId is nullable — some expenses (e.g. staff payroll, general insurance)
 * are org-wide, not tied to one property. Same DB-level-only reference convention
 * as everywhere else (Booking->Property, Invoice->Booking).
 */
@Entity
@Table(name = "expenses")
public class Expense extends TenantScopedEntity {

    @Column(name = "property_id")
    private UUID propertyId;

    @Column(name = "recorded_by_user_id", nullable = false)
    private UUID recordedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    protected Expense() {
    }

    public Expense(UUID propertyId, UUID recordedByUserId, ExpenseCategory category,
                    String description, BigDecimal amount, LocalDate expenseDate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Expense amount must be greater than zero");
        }
        this.propertyId = propertyId;
        this.recordedByUserId = recordedByUserId;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getRecordedByUserId() {
        return recordedByUserId;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }
}
