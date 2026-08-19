package com.hostflow.marketing.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCampaignContentRequest(@NotBlank String content) {
}
