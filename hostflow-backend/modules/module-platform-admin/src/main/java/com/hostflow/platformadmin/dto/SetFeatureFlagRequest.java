package com.hostflow.platformadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SetFeatureFlagRequest(@NotBlank String key, @NotNull Boolean enabled, String description) {
}
