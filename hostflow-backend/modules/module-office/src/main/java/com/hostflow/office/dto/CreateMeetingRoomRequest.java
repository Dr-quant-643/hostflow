package com.hostflow.office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateMeetingRoomRequest(@NotNull UUID propertyId, @NotBlank String name, @NotNull @Positive Integer capacity) {
}
