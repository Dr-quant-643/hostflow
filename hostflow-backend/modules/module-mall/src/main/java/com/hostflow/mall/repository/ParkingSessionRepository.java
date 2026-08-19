package com.hostflow.mall.repository;

import com.hostflow.mall.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID> {
    Optional<ParkingSession> findByPropertyIdAndVehiclePlateAndStatus(
            UUID propertyId, String vehiclePlate, com.hostflow.mall.entity.ParkingSessionStatus status);
}
