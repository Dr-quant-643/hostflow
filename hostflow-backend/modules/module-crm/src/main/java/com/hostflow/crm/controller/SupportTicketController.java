package com.hostflow.crm.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.crm.dto.AssignTicketRequest;
import com.hostflow.crm.dto.CreateSupportTicketRequest;
import com.hostflow.crm.dto.SupportTicketResponse;
import com.hostflow.crm.entity.SupportTicket;
import com.hostflow.crm.entity.TicketProductScope;
import com.hostflow.crm.entity.TicketStatus;
import com.hostflow.crm.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * isAuthenticated() at class level (not PRODUCT_XANUOS-only) — both
 * hostflow-admin
 * and nazilco-admin need this, distinguished by the productScope query
 * param/request field, not by which product authority the caller holds. This
 * matches the "both admin apps query the same table, filtered by productScope"
 * design from the ticket entity's own javadoc.
 */
@RestController
@RequestMapping("/api/v1/crm/support-tickets")
@PreAuthorize("isAuthenticated()")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupportTicketResponse>> create(
            @Valid @RequestBody CreateSupportTicketRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID raisedByUserId = UUID.fromString(jwt.getSubject());
        SupportTicket ticket = ticketService.create(raisedByUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(SupportTicketResponse.from(ticket)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(SupportTicketResponse.from(ticketService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupportTicketResponse>>> list(
            @RequestParam TicketProductScope productScope,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Page<SupportTicketResponse> page = ticketService.list(productScope, status, limit, offset)
                .map(SupportTicketResponse::from);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> assign(
            @PathVariable UUID id, @Valid @RequestBody AssignTicketRequest request) {
        SupportTicket ticket = ticketService.assign(id, request.staffUserId());
        return ResponseEntity.ok(ApiResponse.success(SupportTicketResponse.from(ticket)));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> resolve(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        SupportTicket ticket = ticketService.resolve(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(SupportTicketResponse.from(ticket)));
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> reopen(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        SupportTicket ticket = ticketService.reopen(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(SupportTicketResponse.from(ticket)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> close(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        SupportTicket ticket = ticketService.close(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponse.success(SupportTicketResponse.from(ticket)));
    }
}
