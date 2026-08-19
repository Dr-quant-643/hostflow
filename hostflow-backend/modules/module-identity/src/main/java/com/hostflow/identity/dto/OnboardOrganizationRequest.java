package com.hostflow.identity.dto;

import com.hostflow.identity.entity.OrganizationProduct;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OnboardOrganizationRequest(
        @NotBlank String organizationName,
        @NotBlank String organizationSlug,
        @NotNull OrganizationProduct primaryProduct,
        @NotBlank String adminFirstName,
        @NotBlank String adminLastName,
        @Email @NotBlank String adminEmail
) {
}
