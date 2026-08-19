package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@PreAuthorize("hasAuthority('PRODUCT_NAZILCO')")
public class GuestInvoiceController {

    private final GuestInvoiceQueries invoiceQueries;

    public GuestInvoiceController(GuestInvoiceQueries invoiceQueries) {
        this.invoiceQueries = invoiceQueries;
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<GuestInvoiceQueries.GuestInvoiceRow>>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(invoiceQueries.listMine(UUID.fromString(jwt.getSubject()))));
    }

    @GetMapping("/mine/{id}")
    public ResponseEntity<ApiResponse<GuestInvoiceQueries.GuestInvoiceRow>> mineById(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceQueries.getMineById(UUID.fromString(jwt.getSubject()), id)));
    }
}
