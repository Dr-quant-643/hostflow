package com.hostflow.mall.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParkingSessionEntityTest {

    @Test
    void exit_chargesAtLeastOneHour_evenForShortStay() {
        ParkingSession session = new ParkingSession(java.util.UUID.randomUUID(), "KAA123B");

        session.exit(BigDecimal.valueOf(2.00));

        assertThat(session.getFeeCharged()).isEqualByComparingTo(BigDecimal.valueOf(2.00));
        assertThat(session.getStatus()).isEqualTo(ParkingSessionStatus.COMPLETED);
    }

    @Test
    void exit_calledTwice_throws() {
        ParkingSession session = new ParkingSession(java.util.UUID.randomUUID(), "KAA123B");
        session.exit(BigDecimal.valueOf(2.00));

        assertThatThrownBy(() -> session.exit(BigDecimal.valueOf(2.00)))
                .isInstanceOf(BusinessRuleException.class);
    }
}
