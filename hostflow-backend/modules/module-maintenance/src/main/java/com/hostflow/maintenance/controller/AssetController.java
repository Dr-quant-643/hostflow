package com.hostflow.maintenance.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.maintenance.dto.AssetResponse;
import com.hostflow.maintenance.dto.CreateAssetRequest;
import com.hostflow.maintenance.entity.Asset;
import com.hostflow.maintenance.repository.AssetRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance/assets")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class AssetController {

    private final AssetRepository repository;

    public AssetController(AssetRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<AssetResponse>> create(@Valid @RequestBody CreateAssetRequest request) {
        Asset asset = repository.save(new Asset(request.propertyId(), request.name(), request.category(),
                request.serialNumber(), request.purchaseDate(), request.warrantyExpiryDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(AssetResponse.from(asset)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetResponse>>> listByProperty(@RequestParam UUID propertyId) {
        List<AssetResponse> assets = repository.findByPropertyIdAndActiveTrue(propertyId).stream()
                .map(AssetResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(assets));
    }

    @PatchMapping("/{id}/decommission")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> decommission(@PathVariable UUID id) {
        Asset asset = repository.findById(id).orElseThrow(() ->
                new com.hostflow.common.exception.ResourceNotFoundException("Asset", id));
        asset.decommission();
        return ResponseEntity.noContent().build();
    }
}
