package com.hostflow.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetVarianceResponse(UUID propertyId, String category, LocalDate budgetMonth,
                                      BigDecimal allocatedAmount, BigDecimal actualSpent, BigDecimal variance) {
}
