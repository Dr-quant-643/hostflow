package com.hostflow.review.dto;

import jakarta.validation.constraints.NotBlank;

public record OwnerResponseRequest(@NotBlank String response) {
}
