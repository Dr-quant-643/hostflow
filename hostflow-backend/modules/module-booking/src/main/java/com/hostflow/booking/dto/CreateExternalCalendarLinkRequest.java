package com.hostflow.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateExternalCalendarLinkRequest(
        @NotNull UUID propertyId,
        @NotBlank String icsUrl,
        String label
) {
}
