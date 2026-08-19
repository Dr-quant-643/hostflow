package com.hostflow.office.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.office.dto.CreateRoomBookingRequest;
import com.hostflow.office.dto.RoomBookingResponse;
import com.hostflow.office.entity.RoomBooking;
import com.hostflow.office.service.RoomBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/office/room-bookings")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class RoomBookingController {

    private final RoomBookingService service;

    public RoomBookingController(RoomBookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomBookingResponse>> create(
            @Valid @RequestBody CreateRoomBookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        RoomBooking booking = service.create(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(RoomBookingResponse.from(booking)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RoomBookingResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(RoomBookingResponse.from(service.cancel(id))));
    }
}
