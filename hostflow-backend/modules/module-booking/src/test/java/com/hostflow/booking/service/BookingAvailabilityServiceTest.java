package com.hostflow.booking.service;

import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.entity.BookingStatus;
import com.hostflow.booking.entity.ExternalCalendarBlock;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.booking.repository.ExternalCalendarBlockRepository;
import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingAvailabilityServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ExternalCalendarBlockRepository externalCalendarBlockRepository;

    private BookingAvailabilityService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new BookingAvailabilityService(bookingRepository, externalCalendarBlockRepository);
    }

    @Test
    void assertAvailable_passes_whenNoOverlappingBookings() {
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());

        assertThatCode(() -> service.assertAvailable(
                UUID.randomUUID(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)))
                .doesNotThrowAnyException();
    }

    @Test
    void assertAvailable_throws_whenOverlappingBookingExists() {
        Booking existing = new Booking(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10), java.math.BigDecimal.TEN);
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.assertAvailable(
                UUID.randomUUID(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 8)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void assertAvailable_throws_whenExternalCalendarBlockOverlaps() {
        when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        ExternalCalendarBlock block = new ExternalCalendarBlock(UUID.randomUUID(), UUID.randomUUID(), "uid-1",
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10));
        when(externalCalendarBlockRepository.findOverlapping(any(), any(), any())).thenReturn(List.of(block));

        assertThatThrownBy(() -> service.assertAvailable(
                UUID.randomUUID(), LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 8)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("blocked");
    }
}
