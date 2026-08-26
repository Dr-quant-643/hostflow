package com.hostflow.rental.dto;

import jakarta.validation.constraints.NotBlank;

public record DeclineLeaseRequest(@NotBlank String reason) {
}
