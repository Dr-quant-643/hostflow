package com.hostflow.billing.service;

import com.hostflow.billing.dto.SetBudgetRequest;
import com.hostflow.billing.entity.Budget;
import com.hostflow.billing.entity.ExpenseCategory;
import com.hostflow.billing.repository.BudgetRepository;
import com.hostflow.billing.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private ExpenseRepository expenseRepository;

    private BudgetService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new BudgetService(budgetRepository, expenseRepository);
    }

    @Test
    void setBudget_createsNew_whenNoneExists() {
        UUID propertyId = UUID.randomUUID();
        when(budgetRepository.findByPropertyIdAndCategoryAndBudgetMonth(any(), any(), any())).thenReturn(Optional.empty());
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SetBudgetRequest request = new SetBudgetRequest(
                propertyId, ExpenseCategory.MAINTENANCE, LocalDate.of(2026, 9, 15), BigDecimal.valueOf(500));

        Budget result = service.setBudget(request);

        assertThat(result.getBudgetMonth()).isEqualTo(LocalDate.of(2026, 9, 1));
    }

    @Test
    void setBudget_updatesExisting_ratherThanDuplicating() {
        UUID propertyId = UUID.randomUUID();
        Budget existing = new Budget(propertyId, ExpenseCategory.MAINTENANCE, LocalDate.of(2026, 9, 1), BigDecimal.valueOf(300));
        when(budgetRepository.findByPropertyIdAndCategoryAndBudgetMonth(any(), any(), any())).thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.setBudget(new SetBudgetRequest(propertyId, ExpenseCategory.MAINTENANCE, LocalDate.of(2026, 9, 1), BigDecimal.valueOf(600)));

        assertThat(existing.getAllocatedAmount()).isEqualByComparingTo(BigDecimal.valueOf(600));
        verify(budgetRepository).save(existing);
    }
}
