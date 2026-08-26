package com.hostflow.booking.dto;

import jakarta.validation.constraints.NotBlank;

public record DeclineBookingRequest(@NotBlank String reason) {
}
