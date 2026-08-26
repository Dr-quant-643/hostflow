package com.hostflow.notification.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.notification.dto.CreateSegmentCampaignRequest;
import com.hostflow.notification.entity.SegmentCampaign;
import com.hostflow.notification.repository.SegmentCampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Plain tenant-scoped CRUD -- the actual send (resolving which guests are
 * currently in the target segment and dispatching notifications to them)
 * needs GuestSegmentQueries and NotificationPublisher, both of which this
 * module has no dependency on, so that lives in
 * app/publicapi.SegmentCampaignOrchestrator instead. Mirrors the same split
 * as LeaseService (tenant-scoped CRUD) vs RentalReservationOrchestrator
 * (cross-module orchestration).
 */
@Service
public class SegmentCampaignService {

    private final SegmentCampaignRepository repository;

    public SegmentCampaignService(SegmentCampaignRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SegmentCampaign create(CreateSegmentCampaignRequest request) {
        return repository.save(new SegmentCampaign(request.targetSegment(), request.subject(), request.body()));
    }

    @Transactional(readOnly = true)
    public SegmentCampaign getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SegmentCampaign", id));
    }

    @Transactional(readOnly = true)
    public List<SegmentCampaign> list() {
        return repository.findAll();
    }

    @Transactional
    public SegmentCampaign markSent(UUID id, int recipientCount) {
        SegmentCampaign campaign = getById(id);
        campaign.markSent(recipientCount);
        return campaign;
    }
}
