package com.hostflow.marketing.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.marketing.dto.CreateCampaignRequest;
import com.hostflow.marketing.entity.MarketingCampaign;
import com.hostflow.marketing.repository.MarketingCampaignRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CampaignService {

    private final MarketingCampaignRepository campaignRepository;

    public CampaignService(MarketingCampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Transactional
    public MarketingCampaign create(CreateCampaignRequest request) {
        MarketingCampaign campaign = new MarketingCampaign(
                request.propertyId(), request.name(), request.platform(), request.content());
        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public List<MarketingCampaign> list(int limit, int offset) {
        return campaignRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional(readOnly = true)
    public MarketingCampaign getById(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("MarketingCampaign", campaignId));
    }

    @Transactional
    public MarketingCampaign updateContent(UUID campaignId, String content) {
        MarketingCampaign campaign = getById(campaignId);
        campaign.updateContent(content);
        return campaign;
    }

    @Transactional
    public MarketingCampaign publish(UUID campaignId) {
        MarketingCampaign campaign = getById(campaignId);
        campaign.publish();
        return campaign;
    }

    @Transactional
    public MarketingCampaign archive(UUID campaignId) {
        MarketingCampaign campaign = getById(campaignId);
        campaign.archive();
        return campaign;
    }
}
