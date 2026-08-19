package com.hostflow.marketing.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.marketing.dto.CampaignResponse;
import com.hostflow.marketing.dto.CreateCampaignRequest;
import com.hostflow.marketing.dto.UpdateCampaignContentRequest;
import com.hostflow.marketing.entity.MarketingCampaign;
import com.hostflow.marketing.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketing/campaigns")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> list(
            @RequestParam(defaultValue = "20") int limit, @RequestParam(defaultValue = "0") int offset) {
        List<CampaignResponse> campaigns = campaignService.list(limit, offset).stream()
                .map(CampaignResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(campaigns));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CampaignResponse>> create(@Valid @RequestBody CreateCampaignRequest request) {
        MarketingCampaign campaign = campaignService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CampaignResponse.from(campaign)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(CampaignResponse.from(campaignService.getById(id))));
    }

    @PatchMapping("/{id}/content")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateContent(
            @PathVariable UUID id, @Valid @RequestBody UpdateCampaignContentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(CampaignResponse.from(
                campaignService.updateContent(id, request.content()))));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<CampaignResponse>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(CampaignResponse.from(campaignService.publish(id))));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<CampaignResponse>> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(CampaignResponse.from(campaignService.archive(id))));
    }
}
