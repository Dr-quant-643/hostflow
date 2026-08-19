package com.hostflow.office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record RegisterVisitorRequest(@NotNull UUID propertyId, @NotBlank String fullName, String company, @NotNull Instant expectedAt) {
}
