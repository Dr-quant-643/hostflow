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

/**
 * One row per rent period (typically monthly), generated ahead of time when a
 * lease is activated (see LeaseService.activate()) so the payment schedule is
 * visible/trackable from day one, not created reactively only when rent is paid.
 */
@Entity
@Table(name = "rent_payments")
public class RentPayment extends TenantScopedEntity {

    @Column(name = "lease_id", nullable = false)
    private UUID leaseId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentPaymentStatus status;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    protected RentPayment() {
    }

    public RentPayment(UUID leaseId, LocalDate dueDate, BigDecimal amount) {
        this.leaseId = leaseId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = RentPaymentStatus.DUE;
    }

    public void markPaid(LocalDate paidDate) {
        if (status == RentPaymentStatus.PAID) {
            throw new BusinessRuleException("Payment is already marked PAID");
        }
        this.status = RentPaymentStatus.PAID;
        this.paidDate = paidDate;
    }

    public void markLate() {
        if (status != RentPaymentStatus.DUE) {
            throw new BusinessRuleException("Cannot mark late a payment with status " + status + " (expected DUE)");
        }
        this.status = RentPaymentStatus.LATE;
    }

    public void waive() {
        this.status = RentPaymentStatus.WAIVED;
    }

    public UUID getLeaseId() {
        return leaseId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RentPaymentStatus getStatus() {
        return status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }
}
