package com.hostflow.billing.service;

import com.hostflow.billing.dto.BudgetVarianceResponse;
import com.hostflow.billing.dto.SetBudgetRequest;
import com.hostflow.billing.entity.Budget;
import com.hostflow.billing.entity.ExpenseCategory;
import com.hostflow.billing.repository.BudgetRepository;
import com.hostflow.billing.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Upsert semantics: if a budget already exists for this property+category+month,
     * update its allocation rather than erroring or creating a duplicate — matches
     * the natural "set my budget for this category" mental model rather than
     * forcing a separate create-vs-update API distinction on the frontend.
     */
    @Transactional
    public Budget setBudget(SetBudgetRequest request) {
        LocalDate normalizedMonth = request.budgetMonth().withDayOfMonth(1);
        return budgetRepository.findByPropertyIdAndCategoryAndBudgetMonth(
                        request.propertyId(), request.category(), normalizedMonth)
                .map(existing -> {
                    existing.updateAllocation(request.allocatedAmount());
                    return budgetRepository.save(existing);
                })
                .orElseGet(() -> budgetRepository.save(new Budget(
                        request.propertyId(), request.category(), normalizedMonth, request.allocatedAmount())));
    }

    @Transactional(readOnly = true)
    public List<BudgetVarianceResponse> varianceForMonth(LocalDate month) {
        LocalDate monthStart = month.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1);

        return budgetRepository.findByBudgetMonth(monthStart).stream()
                .map(budget -> {
                    BigDecimal actual = expenseRepository.sumByPropertyCategoryAndMonth(
                            budget.getPropertyId(), budget.getCategory(), monthStart, monthEnd);
                    BigDecimal variance = budget.getAllocatedAmount().subtract(actual);
                    return new BudgetVarianceResponse(budget.getPropertyId(), budget.getCategory().name(),
                            budget.getBudgetMonth(), budget.getAllocatedAmount(), actual, variance);
                })
                .toList();
    }
}
