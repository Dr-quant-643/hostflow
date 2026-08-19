package com.hostflow.crm.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateContactRequest(
        @NotBlank String fullName,
        String email,
        String phone,
        String source
) {
}
