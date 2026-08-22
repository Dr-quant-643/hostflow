package com.hostflow.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record ClaimWorkspaceRequest(@NotBlank String organizationName) {
}
