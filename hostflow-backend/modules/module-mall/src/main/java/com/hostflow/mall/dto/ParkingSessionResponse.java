package com.hostflow.mall.dto;

import com.hostflow.mall.entity.ParkingSession;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParkingSessionResponse(UUID id, String vehiclePlate, Instant enteredAt, Instant exitedAt,
                                      BigDecimal feeCharged, String status) {
    public static ParkingSessionResponse from(ParkingSession s) {
        return new ParkingSessionResponse(s.getId(), s.getVehiclePlate(), s.getEnteredAt(), s.getExitedAt(),
                s.getFeeCharged(), s.getStatus().name());
    }
}
