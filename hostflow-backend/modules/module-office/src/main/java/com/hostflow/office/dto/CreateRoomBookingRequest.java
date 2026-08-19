package com.hostflow.office.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateRoomBookingRequest(@NotNull UUID roomId, @NotNull Instant startsAt, @NotNull Instant endsAt, String purpose) {
}
