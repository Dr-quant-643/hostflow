package com.hostflow.office.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomBookingEntityTest {

    @Test
    void constructor_rejectsEndBeforeStart() {
        Instant now = Instant.now();
        assertThatThrownBy(() -> new RoomBooking(UUID.randomUUID(), UUID.randomUUID(), now, now.minus(1, ChronoUnit.HOURS), "Standup"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void overlaps_detectsOverlappingTimeRanges() {
        Instant start = Instant.now();
        RoomBooking booking = new RoomBooking(UUID.randomUUID(), UUID.randomUUID(),
                start, start.plus(1, ChronoUnit.HOURS), "Planning");

        assertThat(booking.overlaps(start.plus(30, ChronoUnit.MINUTES), start.plus(90, ChronoUnit.MINUTES))).isTrue();
    }

    @Test
    void cancel_thenCancelAgain_throws() {
        Instant start = Instant.now();
        RoomBooking booking = new RoomBooking(UUID.randomUUID(), UUID.randomUUID(),
                start, start.plus(1, ChronoUnit.HOURS), null);

        booking.cancel();

        assertThatThrownBy(booking::cancel).isInstanceOf(BusinessRuleException.class);
    }
}
