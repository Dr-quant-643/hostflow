package com.hostflow.property.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.property.dto.CreatePropertyRequest;
import com.hostflow.property.dto.PropertyResponse;
import com.hostflow.property.dto.SetOccupancyRequest;
import com.hostflow.property.dto.UpdatePropertyDetailsRequest;
import com.hostflow.property.entity.Property;
import com.hostflow.property.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> list(
            @RequestParam(defaultValue = "20") int limit, @RequestParam(defaultValue = "0") int offset) {
        List<PropertyResponse> properties = propertyService.list(limit, offset).stream()
                .map(PropertyResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(properties));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> create(@Valid @RequestBody CreatePropertyRequest request,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        UUID ownerUserId = UUID.fromString(jwt.getSubject());
        Property property = propertyService.create(ownerUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(PropertyResponse.from(property)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PropertyResponse>> getById(@PathVariable UUID id) {
        Property property = propertyService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateDetails(
            @PathVariable UUID id, @RequestBody UpdatePropertyDetailsRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.updateDetails(id, UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> publish(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.publish(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> archive(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.archive(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @PatchMapping("/{id}/unarchive")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> unarchive(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.unarchive(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @PatchMapping("/{id}/occupancy")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> setOccupancy(@PathVariable UUID id,
            @Valid @RequestBody SetOccupancyRequest request, @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.setManualOccupancy(id, UUID.fromString(jwt.getSubject()), request.until());
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }

    @DeleteMapping("/{id}/occupancy")
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<PropertyResponse>> clearOccupancy(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Property property = propertyService.clearManualOccupancy(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(PropertyResponse.from(property)));
    }
}
