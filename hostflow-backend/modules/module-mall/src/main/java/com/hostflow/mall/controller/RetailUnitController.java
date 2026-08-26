package com.hostflow.mall.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.mall.dto.*;
import com.hostflow.mall.entity.RetailTenant;
import com.hostflow.mall.entity.RetailUnit;
import com.hostflow.mall.entity.RetailUnitStatus;
import com.hostflow.mall.repository.RetailUnitRepository;
import com.hostflow.mall.service.RetailTenantAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mall/retail-units")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class RetailUnitController {

    private final RetailUnitRepository unitRepository;
    private final RetailTenantAssignmentService assignmentService;

    public RetailUnitController(RetailUnitRepository unitRepository, RetailTenantAssignmentService assignmentService) {
        this.unitRepository = unitRepository;
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<RetailUnitResponse>> create(@Valid @RequestBody CreateRetailUnitRequest request) {
        RetailUnit unit = unitRepository.save(new RetailUnit(request.propertyId(), request.unitNumber(), request.sizeSqm()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(RetailUnitResponse.from(unit)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RetailUnitResponse>>> listByProperty(@RequestParam UUID propertyId) {
        List<RetailUnitResponse> units = unitRepository.findByPropertyId(propertyId).stream()
                .map(RetailUnitResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(units));
    }

    @PostMapping("/assign-tenant")
    public ResponseEntity<ApiResponse<RetailTenantResponse>> assignTenant(@Valid @RequestBody AssignRetailTenantRequest request) {
        RetailTenant tenant = assignmentService.assign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(RetailTenantResponse.from(tenant)));
    }

    public record OccupancySummary(long total, long occupied, long vacant) {
    }

    /** Tenant-wide (all properties) -- backs the Dashboard's Mall tile. */
    @GetMapping("/occupancy-summary")
    public ResponseEntity<ApiResponse<OccupancySummary>> occupancySummary() {
        long occupied = unitRepository.countByStatus(RetailUnitStatus.OCCUPIED);
        long vacant = unitRepository.countByStatus(RetailUnitStatus.VACANT);
        long underRenovation = unitRepository.countByStatus(RetailUnitStatus.UNDER_RENOVATION);
        return ResponseEntity.ok(ApiResponse.success(new OccupancySummary(occupied + vacant + underRenovation, occupied, vacant)));
    }
}
