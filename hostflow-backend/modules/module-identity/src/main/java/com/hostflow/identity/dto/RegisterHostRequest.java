package com.hostflow.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterHostRequest(
        @NotBlank String organizationName,
        @NotBlank String adminFirstName,
        @NotBlank String adminLastName,
        @Email @NotBlank String adminEmail,
        @NotBlank @Size(min = 8, max = 255) String password) {
}
