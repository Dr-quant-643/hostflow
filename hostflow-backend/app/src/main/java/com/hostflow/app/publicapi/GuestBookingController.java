package com.hostflow.app.publicapi;

import com.hostflow.booking.dto.BookingResponse;
import com.hostflow.booking.dto.CreateBookingRequest;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Guest MUST be authenticated (PRODUCT_NAZILCO) for all of these — only the
 * property browsing/availability endpoints in PublicPropertyController are
 * truly
 * anonymous. This controller's paths are separate from module-booking's
 * /api/v1/bookings/** (staff-facing) specifically to avoid an ambiguous mapping
 * collision, since the two serve fundamentally different tenant-resolution
 * models.
 */
@RestController
@RequestMapping("/api/v1/bookings/public")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class GuestBookingController {

    private final GuestBookingOrchestrator orchestrator;

    public GuestBookingController(GuestBookingOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(@Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        BookingResponse booking = orchestrator.createBooking(guestUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(booking));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> mine(@AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(orchestrator.myBookings(guestUserId)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(@PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.success(orchestrator.cancelBooking(id, guestUserId)));
    }
}
