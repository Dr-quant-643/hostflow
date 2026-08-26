package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance/requests")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class GuestMaintenanceRequestController {

    private final GuestMaintenanceRequestOrchestrator orchestrator;

    public GuestMaintenanceRequestController(GuestMaintenanceRequestOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> send(@Valid @RequestBody GuestMaintenanceRequestRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        orchestrator.send(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>success(null));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<GuestMaintenanceRequestOrchestrator.MyMaintenanceRequestRow>>> mine(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(orchestrator.myRequests(UUID.fromString(jwt.getSubject()))));
    }
}
