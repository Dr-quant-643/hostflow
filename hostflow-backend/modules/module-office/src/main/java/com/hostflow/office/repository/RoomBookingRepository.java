package com.hostflow.office.repository;

import com.hostflow.office.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RoomBookingRepository extends JpaRepository<RoomBooking, UUID> {

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.roomId = :roomId AND rb.status = 'CONFIRMED' " +
            "AND rb.startsAt < :endsAt AND :startsAt < rb.endsAt")
    List<RoomBooking> findOverlapping(@Param("roomId") UUID roomId, @Param("startsAt") Instant startsAt, @Param("endsAt") Instant endsAt);

    List<RoomBooking> findByRoomId(UUID roomId);
}
