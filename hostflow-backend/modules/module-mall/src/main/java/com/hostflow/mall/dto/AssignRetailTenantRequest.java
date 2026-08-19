package com.hostflow.mall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AssignRetailTenantRequest(
        @NotNull UUID retailUnitId, @NotBlank String businessName, String contactEmail, String contactPhone,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyRent, BigDecimal revenueSharePercent) {
}
