package com.hostflow.booking.service;

import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.entity.BookingStatus;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.booking.repository.ExternalCalendarBlockRepository;
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
 *
 * ExternalCalendarBlock overlap (dates blocked by a synced OTA calendar --
 * see ExternalCalendarLink) has NO equivalent DB-level exclusion constraint:
 * it's a separate table, and the block is only as fresh as the last sync
 * poll, not real-time, so a race window here is inherent to iCal sync being
 * pull-based -- this app-level check is the only enforcement, which is an
 * accepted tradeoff of the free/no-partner-API iCal approach.
 */
@Service
public class BookingAvailabilityService {

    private static final List<BookingStatus> BLOCKING_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN);

    private final BookingRepository bookingRepository;
    private final ExternalCalendarBlockRepository externalCalendarBlockRepository;

    public BookingAvailabilityService(BookingRepository bookingRepository,
            ExternalCalendarBlockRepository externalCalendarBlockRepository) {
        this.bookingRepository = bookingRepository;
        this.externalCalendarBlockRepository = externalCalendarBlockRepository;
    }

    public void assertAvailable(UUID propertyId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> overlapping = bookingRepository.findOverlapping(propertyId, checkIn, checkOut, BLOCKING_STATUSES);
        if (!overlapping.isEmpty()) {
            throw new BusinessRuleException("Booking dates overlap an existing reservation");
        }
        if (!externalCalendarBlockRepository.findOverlapping(propertyId, checkIn, checkOut).isEmpty()) {
            throw new BusinessRuleException("These dates are blocked on a connected external calendar");
        }
    }
}
