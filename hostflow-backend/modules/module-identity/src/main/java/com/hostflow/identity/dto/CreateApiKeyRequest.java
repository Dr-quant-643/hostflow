package com.hostflow.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateApiKeyRequest(@NotBlank String name) {
}
