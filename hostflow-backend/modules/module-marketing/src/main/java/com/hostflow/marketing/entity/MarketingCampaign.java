package com.hostflow.marketing.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "marketing_campaigns")
public class MarketingCampaign extends TenantScopedEntity {

    @Column(name = "property_id")
    private UUID propertyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private ContentPlatform platform;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CampaignStatus status;

    protected MarketingCampaign() {
    }

    public MarketingCampaign(UUID propertyId, String name, ContentPlatform platform, String content) {
        this.propertyId = propertyId;
        this.name = name;
        this.platform = platform;
        this.content = content;
        this.status = CampaignStatus.DRAFT;
    }

    public void updateContent(String content) {
        if (status == CampaignStatus.ARCHIVED) {
            throw new BusinessRuleException("Cannot edit an archived campaign");
        }
        this.content = content;
    }

    public void publish() {
        if (status != CampaignStatus.DRAFT) {
            throw new BusinessRuleException("Cannot publish a campaign with status " + status + " (expected DRAFT)");
        }
        this.status = CampaignStatus.PUBLISHED;
    }

    public void archive() {
        this.status = CampaignStatus.ARCHIVED;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getName() {
        return name;
    }

    public ContentPlatform getPlatform() {
        return platform;
    }

    public String getContent() {
        return content;
    }

    public CampaignStatus getStatus() {
        return status;
    }
}
