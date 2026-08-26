package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.notification.dto.CreateSegmentCampaignRequest;
import com.hostflow.notification.dto.SegmentCampaignResponse;
import com.hostflow.notification.entity.SegmentCampaign;
import com.hostflow.notification.service.SegmentCampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Lives in app/publicapi (not module-notification) because /{id}/send needs
 * GuestSegmentQueries + NotificationPublisher via SegmentCampaignOrchestrator
 * -- same cross-module-orchestration split as everywhere else in this
 * package (RentalReservationController, GuestMaintenanceRequestController).
 */
@RestController
@RequestMapping("/api/v1/segment-campaigns")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class SegmentCampaignController {

    private final SegmentCampaignService campaignService;
    private final SegmentCampaignOrchestrator orchestrator;

    public SegmentCampaignController(SegmentCampaignService campaignService, SegmentCampaignOrchestrator orchestrator) {
        this.campaignService = campaignService;
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SegmentCampaignResponse>> create(@Valid @RequestBody CreateSegmentCampaignRequest request) {
        SegmentCampaign campaign = campaignService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(SegmentCampaignResponse.from(campaign)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SegmentCampaignResponse>>> list() {
        List<SegmentCampaignResponse> campaigns = campaignService.list().stream().map(SegmentCampaignResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(campaigns));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SegmentCampaignResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(SegmentCampaignResponse.from(campaignService.getById(id))));
    }

    @PatchMapping("/{id}/send")
    public ResponseEntity<ApiResponse<SegmentCampaignResponse>> send(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        SegmentCampaign campaign = orchestrator.send(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(SegmentCampaignResponse.from(campaign)));
    }
}
