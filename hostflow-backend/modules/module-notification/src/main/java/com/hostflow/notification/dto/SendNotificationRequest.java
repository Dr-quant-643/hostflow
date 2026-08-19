package com.hostflow.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record SendNotificationRequest(
        @NotNull UUID recipientUserId,
        @NotBlank String recipientAddress,
        @NotBlank String templateCode,
        Map<String, String> variables
) {
}
