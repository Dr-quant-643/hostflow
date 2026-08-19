package com.hostflow.rental.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRentalTenantRequest(@NotBlank String fullName, String email, String phone) {
}
