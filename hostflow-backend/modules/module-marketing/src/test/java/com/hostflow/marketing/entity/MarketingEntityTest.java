package com.hostflow.marketing.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarketingCampaignEntityTest {

    private MarketingCampaign newCampaign() {
        return new MarketingCampaign(UUID.randomUUID(), "Summer Sale", ContentPlatform.INSTAGRAM, "Book now and save 20%!");
    }

    @Test
    void newCampaign_startsAsDraft() {
        assertThat(newCampaign().getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    void publish_fromDraft_succeeds() {
        MarketingCampaign campaign = newCampaign();
        campaign.publish();
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PUBLISHED);
    }

    @Test
    void publish_twice_throws() {
        MarketingCampaign campaign = newCampaign();
        campaign.publish();

        assertThatThrownBy(campaign::publish).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void updateContent_onArchivedCampaign_throws() {
        MarketingCampaign campaign = newCampaign();
        campaign.archive();

        assertThatThrownBy(() -> campaign.updateContent("new text")).isInstanceOf(BusinessRuleException.class);
    }
}
