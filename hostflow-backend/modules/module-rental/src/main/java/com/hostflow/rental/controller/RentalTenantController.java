package com.hostflow.rental.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.CreateRentalTenantRequest;
import com.hostflow.rental.dto.RentalTenantResponse;
import com.hostflow.rental.entity.RentalTenant;
import com.hostflow.rental.service.RentalTenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rental/tenants")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class RentalTenantController {

    private final RentalTenantService service;

    public RentalTenantController(RentalTenantService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentalTenantResponse>>> list(
            @RequestParam(defaultValue = "20") int limit, @RequestParam(defaultValue = "0") int offset) {
        List<RentalTenantResponse> tenants = service.list(limit, offset).stream()
                .map(RentalTenantResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(tenants));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RentalTenantResponse>> create(@Valid @RequestBody CreateRentalTenantRequest request) {
        RentalTenant tenant = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(RentalTenantResponse.from(tenant)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalTenantResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(RentalTenantResponse.from(service.getById(id))));
    }
}
