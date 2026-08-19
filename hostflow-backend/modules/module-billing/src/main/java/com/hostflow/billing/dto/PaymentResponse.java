package com.hostflow.billing.dto;

import com.hostflow.billing.entity.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID invoiceId, BigDecimal amount, String providerReference, String status) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getInvoiceId(), payment.getAmount(),
                payment.getProviderReference(), payment.getStatus().name());
    }
}
