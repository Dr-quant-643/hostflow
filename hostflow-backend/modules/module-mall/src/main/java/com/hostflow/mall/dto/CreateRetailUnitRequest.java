package com.hostflow.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRetailUnitRequest(@NotNull UUID propertyId, @NotBlank String unitNumber, BigDecimal sizeSqm) {
}
