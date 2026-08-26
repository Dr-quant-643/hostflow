package com.hostflow.rental.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.CreateLeaseRequest;
import com.hostflow.rental.dto.DeclineLeaseRequest;
import com.hostflow.rental.dto.LeaseResponse;
import com.hostflow.rental.entity.Lease;
import com.hostflow.rental.entity.LeaseStatus;
import com.hostflow.rental.service.LeaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rental/leases")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class LeaseController {

    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeaseResponse>> create(@Valid @RequestBody CreateLeaseRequest request) {
        Lease lease = leaseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(LeaseResponse.from(lease)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaseResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(LeaseResponse.from(leaseService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeaseResponse>>> listByProperty(
            @RequestParam UUID propertyId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(
                leaseService.listByProperty(propertyId, limit, offset).map(LeaseResponse::from)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<LeaseResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(LeaseResponse.from(leaseService.activate(id))));
    }

    @PatchMapping("/{id}/terminate")
    public ResponseEntity<ApiResponse<LeaseResponse>> terminate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(LeaseResponse.from(leaseService.terminate(id))));
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<LeaseResponse>> decline(@PathVariable UUID id,
            @Valid @RequestBody DeclineLeaseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(LeaseResponse.from(leaseService.decline(id, request.reason()))));
    }

    /** Tenant-wide count of DRAFT leases -- backs the XanuOS Bookings nav badge
     *  alongside BookingController's pendingCount(). */
    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse<Long>> pendingCount() {
        return ResponseEntity.ok(ApiResponse.success(leaseService.countDraft()));
    }

    /** Tenant-wide count for any status -- backs dashboard tiles like
     *  "Active Leases" without needing a propertyId. */
    @GetMapping("/status-count")
    public ResponseEntity<ApiResponse<Long>> statusCount(@RequestParam LeaseStatus status) {
        return ResponseEntity.ok(ApiResponse.success(leaseService.countByStatus(status)));
    }
}
