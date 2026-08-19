package com.hostflow.office.dto;

import com.hostflow.office.entity.RoomBooking;

import java.time.Instant;
import java.util.UUID;

public record RoomBookingResponse(UUID id, UUID roomId, Instant startsAt, Instant endsAt, String purpose, String status) {
    public static RoomBookingResponse from(RoomBooking rb) {
        return new RoomBookingResponse(rb.getId(), rb.getRoomId(), rb.getStartsAt(), rb.getEndsAt(), rb.getPurpose(), rb.getStatus().name());
    }
}
