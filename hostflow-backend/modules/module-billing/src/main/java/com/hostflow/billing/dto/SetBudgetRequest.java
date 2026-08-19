package com.hostflow.billing.dto;

import com.hostflow.billing.entity.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SetBudgetRequest(
        UUID propertyId,
        @NotNull ExpenseCategory category,
        @NotNull LocalDate budgetMonth,
        @NotNull @DecimalMin("0.00") BigDecimal allocatedAmount
) {
}
