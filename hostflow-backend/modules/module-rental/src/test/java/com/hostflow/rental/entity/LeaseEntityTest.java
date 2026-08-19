package com.hostflow.rental.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeaseEntityTest {

    @Test
    void constructor_rejectsEndDateBeforeStart() {
        assertThatThrownBy(() -> new Lease(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 1, 1), BigDecimal.valueOf(1000), null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void constructor_rejectsZeroRent() {
        assertThatThrownBy(() -> new Lease(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 1), BigDecimal.ZERO, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void fullLifecycle_draftToActiveToTerminated() {
        Lease lease = new Lease(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 1), BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

        lease.activate();
        assertThat(lease.getStatus()).isEqualTo(LeaseStatus.ACTIVE);

        lease.terminate();
        assertThat(lease.getStatus()).isEqualTo(LeaseStatus.TERMINATED);
    }

    @Test
    void terminate_beforeActive_throws() {
        Lease lease = new Lease(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 1), BigDecimal.valueOf(1000), null);

        assertThatThrownBy(lease::terminate)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expected ACTIVE");
    }
}
