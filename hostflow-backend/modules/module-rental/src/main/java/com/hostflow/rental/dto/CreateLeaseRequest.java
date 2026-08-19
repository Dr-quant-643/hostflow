package com.hostflow.rental.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateLeaseRequest(
        @NotNull UUID propertyId,
        @NotNull UUID tenantIdRef,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyRent,
        BigDecimal securityDeposit
) {
}
