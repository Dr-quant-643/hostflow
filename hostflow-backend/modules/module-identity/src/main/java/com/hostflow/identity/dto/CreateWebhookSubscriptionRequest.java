package com.hostflow.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWebhookSubscriptionRequest(@NotBlank String url, @NotBlank String eventType) {
}
