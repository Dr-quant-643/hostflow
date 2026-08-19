package com.hostflow.rental.dto;

import com.hostflow.rental.entity.RentPayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RentPaymentResponse(UUID id, UUID leaseId, LocalDate dueDate, BigDecimal amount,
                                   String status, LocalDate paidDate) {

    public static RentPaymentResponse from(RentPayment payment) {
        return new RentPaymentResponse(payment.getId(), payment.getLeaseId(), payment.getDueDate(),
                payment.getAmount(), payment.getStatus().name(), payment.getPaidDate());
    }
}
