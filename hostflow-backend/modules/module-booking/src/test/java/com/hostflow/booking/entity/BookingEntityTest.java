package com.hostflow.booking.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingEntityTest {

    private Booking newBooking(LocalDate checkIn, LocalDate checkOut) {
        return new Booking(UUID.randomUUID(), UUID.randomUUID(), checkIn, checkOut, BigDecimal.valueOf(500));
    }

    @Test
    void constructor_rejectsCheckOutNotAfterCheckIn() {
        LocalDate day = LocalDate.of(2026, 9, 1);

        assertThatThrownBy(() -> newBooking(day, day))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Check-out date must be after check-in date");
    }

    @Test
    void statusLifecycle_progressesInOrder() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        booking.confirm();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        booking.checkIn();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);
        booking.checkOut();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_OUT);
    }

    @Test
    void checkIn_beforeConfirm_throwsBusinessRuleException() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThatThrownBy(booking::checkIn)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expected CONFIRMED");
    }

    @Test
    void cancel_afterCheckOut_throwsBusinessRuleException() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));
        booking.confirm();
        booking.checkIn();
        booking.checkOut();

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already CHECKED_OUT");
    }

    @Test
    void overlaps_detectsPartialOverlap() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10));

        assertThat(booking.overlaps(LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 15))).isTrue();
    }

    @Test
    void overlaps_returnsFalse_whenNewBookingStartsExactlyOnOldCheckOutDay() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10));

        // Half-open interval: checking in the same day another guest checks out is allowed.
        assertThat(booking.overlaps(LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 15))).isFalse();
    }

    @Test
    void overlaps_returnsFalse_forCompletelySeparateDateRanges() {
        Booking booking = newBooking(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThat(booking.overlaps(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5))).isFalse();
    }
}
