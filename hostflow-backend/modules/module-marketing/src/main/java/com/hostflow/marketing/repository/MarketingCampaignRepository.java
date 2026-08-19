package com.hostflow.marketing.repository;

import com.hostflow.marketing.entity.MarketingCampaign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, UUID> {
    List<MarketingCampaign> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
