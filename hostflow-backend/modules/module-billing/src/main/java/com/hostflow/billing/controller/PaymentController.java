package com.hostflow.billing.controller;

import com.hostflow.billing.dto.PaymentResponse;
import com.hostflow.billing.dto.RecordPaymentRequest;
import com.hostflow.billing.entity.Payment;
import com.hostflow.billing.service.PaymentService;
import com.hostflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/payments")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> record(@Valid @RequestBody RecordPaymentRequest request) {
        Payment payment = paymentService.recordAttempt(request.invoiceId(), request.amount(), request.providerReference());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(PaymentResponse.from(payment)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> listByInvoice(@RequestParam UUID invoiceId) {
        List<PaymentResponse> payments = paymentService.listByInvoice(invoiceId).stream().map(PaymentResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PatchMapping("/{id}/succeed")
    public ResponseEntity<ApiResponse<PaymentResponse>> succeed(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(PaymentResponse.from(paymentService.markSucceeded(id))));
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<ApiResponse<PaymentResponse>> fail(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(PaymentResponse.from(paymentService.markFailed(id))));
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(PaymentResponse.from(paymentService.refund(id))));
    }
}
