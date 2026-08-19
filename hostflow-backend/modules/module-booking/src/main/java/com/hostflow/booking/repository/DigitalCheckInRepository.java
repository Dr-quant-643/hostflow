package com.hostflow.booking.repository;

import com.hostflow.booking.entity.DigitalCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DigitalCheckInRepository extends JpaRepository<DigitalCheckIn, UUID> {
    Optional<DigitalCheckIn> findByBookingId(UUID bookingId);
}
