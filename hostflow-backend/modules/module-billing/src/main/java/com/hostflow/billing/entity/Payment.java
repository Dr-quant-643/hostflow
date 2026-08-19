package com.hostflow.billing.entity;

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
@Table(name = "payments")
public class Payment extends TenantScopedEntity {

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "provider_reference")
    private String providerReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(UUID invoiceId, BigDecimal amount, String providerReference) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.providerReference = providerReference;
        this.status = PaymentStatus.PENDING;
    }

    public void markSucceeded() {
        if (status != PaymentStatus.PENDING) {
            throw new BusinessRuleException("Cannot succeed a payment with status " + status + " (expected PENDING)");
        }
        this.status = PaymentStatus.SUCCEEDED;
    }

    public void markFailed() {
        if (status != PaymentStatus.PENDING) {
            throw new BusinessRuleException("Cannot fail a payment with status " + status + " (expected PENDING)");
        }
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (status != PaymentStatus.SUCCEEDED) {
            throw new BusinessRuleException("Cannot refund a payment with status " + status + " (expected SUCCEEDED)");
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
