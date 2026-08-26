package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.CreateWebhookSubscriptionRequest;
import com.hostflow.identity.dto.WebhookSubscriptionResponse;
import com.hostflow.identity.service.WebhookSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class WebhookSubscriptionController {

    private final WebhookSubscriptionService service;

    public WebhookSubscriptionController(WebhookSubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WebhookSubscriptionResponse>> create(@Valid @RequestBody CreateWebhookSubscriptionRequest request) {
        var subscription = service.create(request.url(), request.eventType());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(WebhookSubscriptionResponse.from(subscription)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WebhookSubscriptionResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(
                service.list().stream().map(WebhookSubscriptionResponse::from).toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
