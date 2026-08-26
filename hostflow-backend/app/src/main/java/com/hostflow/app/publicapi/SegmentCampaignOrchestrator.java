package com.hostflow.app.publicapi;

import com.hostflow.notification.entity.NotificationChannel;
import com.hostflow.notification.entity.SegmentCampaign;
import com.hostflow.notification.messaging.NotificationPublisher;
import com.hostflow.notification.service.SegmentCampaignService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Closes the loop on guest segmentation: GuestSegmentQueries classifies
 * guests (VIP/REPEAT/AT_RISK/NEW/ACTIVE_TENANT) as a read-only report; this
 * is what lets an owner act on it -- write a message once, send it to every
 * guest currently in a segment, e.g. a discount offer to AT_RISK guests to
 * win them back, or early access for VIPs. "ALL" targets every guest in
 * GuestSegmentQueries.segmentsForOwner regardless of segment.
 *
 * NotificationPublisher.publish() is used directly rather than
 * NotificationService.send() -- campaign subject/body is free-text authored
 * per-campaign, not a pre-seeded NotificationTemplate looked up by code, so
 * the template-repository lookup NotificationService.send() does doesn't
 * apply here.
 */
@Component
public class SegmentCampaignOrchestrator {

    private final SegmentCampaignService campaignService;
    private final GuestSegmentQueries guestSegmentQueries;
    private final NotificationPublisher notificationPublisher;

    public SegmentCampaignOrchestrator(SegmentCampaignService campaignService, GuestSegmentQueries guestSegmentQueries,
            NotificationPublisher notificationPublisher) {
        this.campaignService = campaignService;
        this.guestSegmentQueries = guestSegmentQueries;
        this.notificationPublisher = notificationPublisher;
    }

    public SegmentCampaign send(UUID campaignId, UUID ownerUserId) {
        SegmentCampaign campaign = campaignService.getById(campaignId);
        List<GuestSegmentQueries.GuestSegmentRow> segments = guestSegmentQueries.segmentsForOwner(ownerUserId);

        int sent = 0;
        for (GuestSegmentQueries.GuestSegmentRow guest : segments) {
            if (!"ALL".equals(campaign.getTargetSegment()) && !guest.segment().equals(campaign.getTargetSegment())) {
                continue;
            }
            if (guest.email() == null) {
                continue;
            }
            notificationPublisher.publish(guest.guestUserId(), guest.email(), "segment_campaign",
                    NotificationChannel.EMAIL, campaign.getSubject(), personalize(campaign.getBody(), guest));
            sent++;
        }

        return campaignService.markSent(campaignId, sent);
    }

    private String personalize(String body, GuestSegmentQueries.GuestSegmentRow guest) {
        String name = guest.name() != null ? guest.name() : "there";
        return body.replace("{{guest_name}}", name);
    }
}
