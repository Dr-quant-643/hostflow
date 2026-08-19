package com.hostflow.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateInvoiceRequest(
        UUID bookingId,
        @NotNull UUID billedUserId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull @FutureOrPresent LocalDate dueDate
) {
}
