package com.hostflow.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record RenameOrganizationRequest(@NotBlank String name) {
}
