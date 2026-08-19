package com.hostflow.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateMallEventRequest(@NotNull UUID propertyId, @NotBlank String title, String description,
                                      @NotNull Instant startsAt, @NotNull Instant endsAt) {
}
