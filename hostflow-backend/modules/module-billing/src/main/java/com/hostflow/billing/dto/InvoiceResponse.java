package com.hostflow.billing.dto;

import com.hostflow.billing.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(UUID id, UUID bookingId, UUID billedUserId, BigDecimal amount, LocalDate dueDate, String status) {

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(), invoice.getBookingId(), invoice.getBilledUserId(),
                invoice.getAmount(), invoice.getDueDate(), invoice.getStatus().name());
    }
}
