package com.hostflow.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSegmentCampaignRequest(
        @NotBlank String targetSegment,
        @NotBlank String subject,
        @NotBlank String body
) {
}
