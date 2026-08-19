package com.hostflow.billing.dto;

import com.hostflow.billing.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(UUID id, UUID propertyId, String category, String description,
                               BigDecimal amount, LocalDate expenseDate) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getPropertyId(), expense.getCategory().name(),
                expense.getDescription(), expense.getAmount(), expense.getExpenseDate());
    }
}
