package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.rental.dto.LeaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * A signed-in NazilCo guest reserving a MONTHLY property directly, without
 * going through RentalInquiryController first -- see
 * RentalReservationOrchestrator for why this is a deliberately separate,
 * un-vetted path from staff-created leases.
 */
@RestController
@RequestMapping("/api/v1/rental/reservations")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class RentalReservationController {

    private final RentalReservationOrchestrator orchestrator;

    public RentalReservationController(RentalReservationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeaseResponse>> reserve(@Valid @RequestBody ReserveRentalRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        LeaseResponse lease = orchestrator.reserve(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(lease));
    }
}
