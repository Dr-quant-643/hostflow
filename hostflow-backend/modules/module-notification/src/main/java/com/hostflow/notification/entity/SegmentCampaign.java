package com.hostflow.notification.entity;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A message an owner sends to every guest currently in a given
 * GuestSegmentQueries segment (VIP, REPEAT, AT_RISK, NEW, ACTIVE_TENANT, or
 * ALL) -- the "close the loop on segmentation" feature: the segment engine
 * classifies guests, this is what lets an owner act on that classification
 * instead of it being read-only. targetSegment is a plain String (not the
 * GuestSegment enum) because that enum lives in app/publicapi
 * (GuestSegmentQueries) and this module has no dependency on app -- same
 * reasoning as storing status/category as strings elsewhere in this codebase
 * when the authoritative enum lives in a different module.
 *
 * Deliberately NOT reusing MarketingCampaign (module-marketing): that entity
 * is content-authoring for social/ads platforms (FACEBOOK, TIKTOK, GOOGLE_ADS,
 * ...) with no audience/recipient concept at all -- wrong shape for "send this
 * to a specific guest segment via email/SMS."
 */
@Entity
@Table(name = "segment_campaigns")
public class SegmentCampaign extends TenantScopedEntity {

    @Column(name = "target_segment", nullable = false)
    private String targetSegment;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SegmentCampaignStatus status;

    @Column(name = "recipient_count")
    private Integer recipientCount;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected SegmentCampaign() {
    }

    public SegmentCampaign(String targetSegment, String subject, String body) {
        this.targetSegment = targetSegment;
        this.subject = subject;
        this.body = body;
        this.status = SegmentCampaignStatus.DRAFT;
    }

    public void markSent(int recipientCount) {
        if (status != SegmentCampaignStatus.DRAFT) {
            throw new BusinessRuleException("Cannot send a campaign with status " + status + " (expected DRAFT)");
        }
        this.status = SegmentCampaignStatus.SENT;
        this.recipientCount = recipientCount;
        this.sentAt = Instant.now();
    }

    public String getTargetSegment() {
        return targetSegment;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public SegmentCampaignStatus getStatus() {
        return status;
    }

    public Integer getRecipientCount() {
        return recipientCount;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
