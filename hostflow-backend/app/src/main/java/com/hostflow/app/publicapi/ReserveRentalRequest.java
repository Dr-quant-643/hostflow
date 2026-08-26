package com.hostflow.app.publicapi;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ReserveRentalRequest(
        @NotNull UUID propertyId,
        @NotNull LocalDate moveInDate,
        @NotNull @Min(1) @Max(36) Integer months) {
}
