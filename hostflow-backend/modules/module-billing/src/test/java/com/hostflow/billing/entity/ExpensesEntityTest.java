package com.hostflow.billing.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseEntityTest {

    @Test
    void constructor_rejectsZeroAmount() {
        assertThatThrownBy(() -> new Expense(null, UUID.randomUUID(), ExpenseCategory.UTILITIES,
                "Water bill", BigDecimal.ZERO, LocalDate.now()))
                .isInstanceOf(BusinessRuleException.class);
    }
}
