package com.hostflow.billing.repository;

import com.hostflow.billing.entity.Budget;
import com.hostflow.billing.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByBudgetMonth(LocalDate budgetMonth);
    Optional<Budget> findByPropertyIdAndCategoryAndBudgetMonth(UUID propertyId, ExpenseCategory category, LocalDate budgetMonth);
}
