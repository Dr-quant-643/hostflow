package com.hostflow.booking.service;

import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.entity.BookingStatus;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.common.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The application-level pre-check (findOverlapping) remains as a fast-fail for
 * the common case — most requests will never even reach the database's
 * excl_bookings_no_overlap constraint, since this check catches the overlap
 * first and returns a clean error immediately. The DB constraint (V23 migration)
 * is the BACKSTOP for the genuine race window between this check and the
 * subsequent INSERT — this method alone no longer needs to be "the" defense,
 * just the fast, common-case one.
 */
@Service
public class BookingAvailabilityService {

    private static final List<BookingStatus> BLOCKING_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN);

    private final BookingRepository bookingRepository;

    public BookingAvailabilityService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void assertAvailable(UUID propertyId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> overlapping = bookingRepository.findOverlapping(propertyId, checkIn, checkOut, BLOCKING_STATUSES);
        if (!overlapping.isEmpty()) {
            throw new BusinessRuleException("Booking dates overlap an existing reservation");
        }
    }
}
