package com.hostflow.booking.controller;

import com.hostflow.booking.dto.CreateExternalCalendarLinkRequest;
import com.hostflow.booking.dto.ExternalCalendarLinkResponse;
import com.hostflow.booking.entity.ExternalCalendarLink;
import com.hostflow.booking.service.ExternalCalendarLinkService;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings/external-calendars")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class ExternalCalendarLinkController {

    private final ExternalCalendarLinkService service;

    public ExternalCalendarLinkController(ExternalCalendarLinkService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExternalCalendarLinkResponse>> create(@Valid @RequestBody CreateExternalCalendarLinkRequest request) {
        ExternalCalendarLink link = service.create(request.propertyId(), request.icsUrl(), request.label());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ExternalCalendarLinkResponse.from(link)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExternalCalendarLinkResponse>>> listByProperty(@RequestParam UUID propertyId) {
        List<ExternalCalendarLinkResponse> links = service.listByProperty(propertyId).stream()
                .map(ExternalCalendarLinkResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(links));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
