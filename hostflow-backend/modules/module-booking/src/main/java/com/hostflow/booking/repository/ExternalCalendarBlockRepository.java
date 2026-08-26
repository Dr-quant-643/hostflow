package com.hostflow.booking.repository;

import com.hostflow.booking.entity.ExternalCalendarBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalCalendarBlockRepository extends JpaRepository<ExternalCalendarBlock, UUID> {

    Optional<ExternalCalendarBlock> findByLinkIdAndExternalUid(UUID linkId, String externalUid);

    List<ExternalCalendarBlock> findByLinkId(UUID linkId);

    void deleteByLinkIdAndExternalUidNotIn(UUID linkId, List<String> stillPresentUids);

    /** Same half-open interval overlap semantics as
     *  BookingRepository.findOverlapping/Booking.overlaps(). */
    @Query("""
            SELECT b FROM ExternalCalendarBlock b
            WHERE b.propertyId = :propertyId
            AND b.startDate < :checkOut
            AND :checkIn < b.endDate
            """)
    List<ExternalCalendarBlock> findOverlapping(@Param("propertyId") UUID propertyId,
                                                 @Param("checkIn") LocalDate checkIn,
                                                 @Param("checkOut") LocalDate checkOut);
}
