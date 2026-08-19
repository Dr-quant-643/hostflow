package com.hostflow.marketing.dto;

import com.hostflow.marketing.entity.MarketingCampaign;

import java.util.UUID;

public record CampaignResponse(UUID id, UUID propertyId, String name, String platform, String content, String status) {

    public static CampaignResponse from(MarketingCampaign campaign) {
        return new CampaignResponse(
                campaign.getId(), campaign.getPropertyId(), campaign.getName(),
                campaign.getPlatform().name(), campaign.getContent(), campaign.getStatus().name());
    }
}
