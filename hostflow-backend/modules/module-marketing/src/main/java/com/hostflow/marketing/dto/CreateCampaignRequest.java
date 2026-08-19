package com.hostflow.marketing.dto;

import com.hostflow.marketing.entity.ContentPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCampaignRequest(
        UUID propertyId,
        @NotBlank String name,
        @NotNull ContentPlatform platform,
        @NotBlank String content
) {
}
