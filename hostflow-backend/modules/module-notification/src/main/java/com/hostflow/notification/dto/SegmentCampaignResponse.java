package com.hostflow.notification.dto;

import com.hostflow.notification.entity.SegmentCampaign;

import java.time.Instant;
import java.util.UUID;

public record SegmentCampaignResponse(
        UUID id, String targetSegment, String subject, String body, String status,
        Integer recipientCount, Instant sentAt
) {
    public static SegmentCampaignResponse from(SegmentCampaign campaign) {
        return new SegmentCampaignResponse(campaign.getId(), campaign.getTargetSegment(), campaign.getSubject(),
                campaign.getBody(), campaign.getStatus().name(), campaign.getRecipientCount(), campaign.getSentAt());
    }
}
