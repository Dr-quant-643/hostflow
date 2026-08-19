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
 * References Booking by UUID only (nullable — not every invoice originates from a
 * booking; e.g. a manual charge or a subscription invoice), same DB-level-only
 * convention used throughout (module-booking -> module-property, etc.).
 */
@Entity
@Table(name = "invoices")
public class Invoice extends TenantScopedEntity {

    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "billed_user_id", nullable = false)
    private UUID billedUserId;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    protected Invoice() {
    }

    public Invoice(UUID bookingId, UUID billedUserId, BigDecimal amount, LocalDate dueDate) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invoice amount must be greater than zero");
        }
        this.bookingId = bookingId;
        this.billedUserId = billedUserId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = InvoiceStatus.DRAFT;
    }

    public void issue() {
        if (status != InvoiceStatus.DRAFT) {
            throw new BusinessRuleException("Cannot issue an invoice with status " + status + " (expected DRAFT)");
        }
        this.status = InvoiceStatus.ISSUED;
    }

    public void markPaid() {
        if (status != InvoiceStatus.ISSUED && status != InvoiceStatus.OVERDUE) {
            throw new BusinessRuleException("Cannot mark paid an invoice with status " + status);
        }
        this.status = InvoiceStatus.PAID;
    }

    public void markOverdue() {
        if (status != InvoiceStatus.ISSUED) {
            throw new BusinessRuleException("Cannot mark overdue an invoice with status " + status + " (expected ISSUED)");
        }
        this.status = InvoiceStatus.OVERDUE;
    }

    public void voidInvoice() {
        if (status == InvoiceStatus.PAID) {
            throw new BusinessRuleException("Cannot void a PAID invoice");
        }
        this.status = InvoiceStatus.VOID;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getBilledUserId() {
        return billedUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
}
