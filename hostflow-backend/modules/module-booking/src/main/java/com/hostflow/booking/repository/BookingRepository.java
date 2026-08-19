package com.hostflow.booking.repository;

import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Finds bookings for a property that overlap a given date range and are in a
     * status that actually occupies the calendar (PENDING/CONFIRMED/CHECKED_IN —
     * CANCELLED and CHECKED_OUT bookings don't block new bookings on those same dates).
     * Uses the same half-open interval logic as Booking.overlaps() — kept consistent
     * intentionally, see PropertyAvailabilityService's javadoc for why both exist.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.propertyId = :propertyId
            AND b.status IN :blockingStatuses
            AND b.checkIn < :checkOut
            AND :checkIn < b.checkOut
            """)
    List<Booking> findOverlapping(@Param("propertyId") UUID propertyId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut,
                                   @Param("blockingStatuses") List<BookingStatus> blockingStatuses);

    List<Booking> findByGuestUserId(UUID guestUserId);
}
