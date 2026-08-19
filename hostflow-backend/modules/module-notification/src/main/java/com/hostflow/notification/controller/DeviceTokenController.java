package com.hostflow.notification.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.notification.dto.RegisterDeviceTokenRequest;
import com.hostflow.notification.service.DeviceTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * No product-scope restriction — both XanuOS staff and NazilCo guests can
 * register a device for push, and both are identified the same way (Keycloak
 * subject id), so a single endpoint serves either.
 */
@RestController
@RequestMapping("/api/v1/notifications/devices")
public class DeviceTokenController {

    private final DeviceTokenService service;

    public DeviceTokenController(DeviceTokenService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterDeviceTokenRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        service.register(UUID.fromString(jwt.getSubject()), request.deviceToken(), request.platform());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>success(null));
    }

    @PostMapping("/unregister")
    public ResponseEntity<ApiResponse<Void>> unregister(@Valid @RequestBody RegisterDeviceTokenRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        service.unregister(UUID.fromString(jwt.getSubject()), request.deviceToken());
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
