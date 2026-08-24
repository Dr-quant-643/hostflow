package com.hostflow.property.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record SetOccupancyRequest(@NotNull Instant until) {
}
