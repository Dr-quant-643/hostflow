package com.hostflow.billing.repository;

import com.hostflow.billing.entity.Expense;
import com.hostflow.billing.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Expense> findByCategory(ExpenseCategory category, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
            "WHERE (:propertyId IS NULL OR e.propertyId = :propertyId) " +
            "AND e.category = :category " +
            "AND e.expenseDate >= :monthStart AND e.expenseDate < :monthEnd")
    BigDecimal sumByPropertyCategoryAndMonth(@Param("propertyId") UUID propertyId,
                                              @Param("category") ExpenseCategory category,
                                              @Param("monthStart") LocalDate monthStart,
                                              @Param("monthEnd") LocalDate monthEnd);

    List<Expense> findByExpenseDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.category = :category")
    BigDecimal sumByCategory(@Param("category") ExpenseCategory category);
}
