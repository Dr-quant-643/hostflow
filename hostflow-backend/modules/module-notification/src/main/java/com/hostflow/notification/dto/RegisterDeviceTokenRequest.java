package com.hostflow.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceTokenRequest(
        @NotBlank String deviceToken,
        String platform
) {
}
