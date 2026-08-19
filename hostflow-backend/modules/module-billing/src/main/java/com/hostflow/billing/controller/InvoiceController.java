package com.hostflow.billing.controller;

import com.hostflow.billing.dto.*;
import com.hostflow.billing.entity.Invoice;
import com.hostflow.billing.service.InvoiceService;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/invoices")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> list(
            @RequestParam(defaultValue = "20") int limit, @RequestParam(defaultValue = "0") int offset) {
        List<InvoiceResponse> invoices = invoiceService.list(limit, offset).stream()
                .map(InvoiceResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(@Valid @RequestBody CreateInvoiceRequest request) {
        Invoice invoice = invoiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(InvoiceResponse.from(invoice)));
    }

    /**
     * The Batch API implementation, per the resolved open item. Always returns 200
     * (not 201/207) since the response body itself carries per-row success/failure —
     * an HTTP-level status can't represent "37 succeeded, 3 failed" accurately.
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<BatchCreateInvoicesResponse>> createBatch(
            @Valid @RequestBody BatchCreateInvoicesRequest request) {
        List<BatchInvoiceResult> results = invoiceService.createBatch(request.invoices());
        return ResponseEntity.ok(ApiResponse.success(BatchCreateInvoicesResponse.from(results)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(@PathVariable UUID id) {
        Invoice invoice = invoiceService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(InvoiceResponse.from(invoice)));
    }

    @PatchMapping("/{id}/issue")
    public ResponseEntity<ApiResponse<InvoiceResponse>> issue(@PathVariable UUID id) {
        Invoice invoice = invoiceService.issue(id);
        return ResponseEntity.ok(ApiResponse.success(InvoiceResponse.from(invoice)));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markPaid(@PathVariable UUID id) {
        Invoice invoice = invoiceService.markPaid(id);
        return ResponseEntity.ok(ApiResponse.success(InvoiceResponse.from(invoice)));
    }
}
