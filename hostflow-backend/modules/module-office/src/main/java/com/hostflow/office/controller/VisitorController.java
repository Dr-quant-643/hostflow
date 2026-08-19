package com.hostflow.office.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.office.dto.RegisterVisitorRequest;
import com.hostflow.office.dto.VisitorResponse;
import com.hostflow.office.entity.Visitor;
import com.hostflow.office.service.VisitorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/office/visitors")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class VisitorController {

    private final VisitorService service;

    public VisitorController(VisitorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VisitorResponse>> register(
            @Valid @RequestBody RegisterVisitorRequest request, @AuthenticationPrincipal Jwt jwt) {
        Visitor visitor = service.register(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(VisitorResponse.from(visitor)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VisitorResponse>>> list(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(service.listByProperty(propertyId, limit, offset).map(VisitorResponse::from)));
    }

    @PatchMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<VisitorResponse>> checkIn(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(VisitorResponse.from(service.checkIn(id))));
    }

    @PatchMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<VisitorResponse>> checkOut(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(VisitorResponse.from(service.checkOut(id))));
    }
}
