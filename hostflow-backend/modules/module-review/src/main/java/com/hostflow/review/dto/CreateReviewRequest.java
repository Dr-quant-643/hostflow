package com.hostflow.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequest(@NotNull UUID bookingId, @NotNull @Min(1) @Max(5) Integer rating, String comment) {
}
