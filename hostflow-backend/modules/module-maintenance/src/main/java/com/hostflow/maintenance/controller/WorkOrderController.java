package com.hostflow.maintenance.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.maintenance.dto.*;
import com.hostflow.maintenance.entity.WorkOrder;
import com.hostflow.maintenance.service.WorkOrderService;
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
@RequestMapping("/api/v1/maintenance/work-orders")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkOrderResponse>> create(
            @Valid @RequestBody CreateWorkOrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        WorkOrder wo = workOrderService.create(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(WorkOrderResponse.from(wo)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(WorkOrderResponse.from(workOrderService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkOrderResponse>>> listByProperty(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(
                workOrderService.listByProperty(propertyId, limit, offset).map(WorkOrderResponse::from)));
    }

    @GetMapping("/my-assignments")
    public ResponseEntity<ApiResponse<Page<WorkOrderResponse>>> myAssignments(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        UUID technicianId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(
                workOrderService.listByTechnician(technicianId, limit, offset).map(WorkOrderResponse::from)));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> assign(
            @PathVariable UUID id, @Valid @RequestBody AssignTechnicianRequest request) {
        return ResponseEntity.ok(ApiResponse.success(WorkOrderResponse.from(
                workOrderService.assign(id, request.technicianUserId()))));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> start(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(WorkOrderResponse.from(workOrderService.startWork(id))));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> complete(
            @PathVariable UUID id, @RequestBody CompleteWorkOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(WorkOrderResponse.from(
                workOrderService.complete(id, request.resolutionNotes()))));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(WorkOrderResponse.from(workOrderService.cancel(id))));
    }
}
