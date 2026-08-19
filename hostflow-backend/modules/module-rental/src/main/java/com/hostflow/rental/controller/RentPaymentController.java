package com.hostflow.rental.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.RentPaymentResponse;
import com.hostflow.rental.service.RentPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rental/rent-payments")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class RentPaymentController {

    private final RentPaymentService service;

    public RentPaymentController(RentPaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentPaymentResponse>>> listByLease(@RequestParam UUID leaseId) {
        List<RentPaymentResponse> payments = service.listByLease(leaseId).stream()
                .map(RentPaymentResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<RentPaymentResponse>> markPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(RentPaymentResponse.from(service.markPaid(id))));
    }

    @PatchMapping("/{id}/waive")
    public ResponseEntity<ApiResponse<RentPaymentResponse>> waive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(RentPaymentResponse.from(service.waive(id))));
    }
}
