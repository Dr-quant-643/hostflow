package com.hostflow.mall.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.mall.dto.ParkingEntryRequest;
import com.hostflow.mall.dto.ParkingSessionResponse;
import com.hostflow.mall.entity.ParkingSession;
import com.hostflow.mall.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mall/parking")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/enter")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> enter(@Valid @RequestBody ParkingEntryRequest request) {
        ParkingSession session = parkingService.enter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ParkingSessionResponse.from(session)));
    }

    @PatchMapping("/{id}/exit")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> exit(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ParkingSessionResponse.from(parkingService.exit(id))));
    }
}
