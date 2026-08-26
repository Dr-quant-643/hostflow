package com.hostflow.notification.repository;

import com.hostflow.notification.entity.SegmentCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SegmentCampaignRepository extends JpaRepository<SegmentCampaign, UUID> {
}
