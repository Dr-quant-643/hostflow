package com.hostflow.booking.controller;

import com.hostflow.booking.dto.BookingResponse;
import com.hostflow.booking.dto.CreateBookingRequest;
import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.service.BookingService;
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

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> list(
            @RequestParam(defaultValue = "20") int limit, @RequestParam(defaultValue = "0") int offset) {
        List<BookingResponse> bookings = bookingService.list(limit, offset).stream()
                .map(BookingResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
    public ResponseEntity<ApiResponse<BookingResponse>> create(@Valid @RequestBody CreateBookingRequest request,
                                                                 @AuthenticationPrincipal Jwt jwt) {
        UUID guestUserId = UUID.fromString(jwt.getSubject());
        Booking booking = bookingService.create(guestUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(BookingResponse.from(booking)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable UUID id) {
        Booking booking = bookingService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(BookingResponse.from(booking)));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookingResponse>> confirm(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Booking booking = bookingService.confirm(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(BookingResponse.from(booking)));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        Booking booking = bookingService.cancel(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(BookingResponse.from(booking)));
    }
}
