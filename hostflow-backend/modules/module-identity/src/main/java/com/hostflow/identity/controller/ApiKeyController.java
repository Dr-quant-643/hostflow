package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.ApiKeyResponse;
import com.hostflow.identity.dto.CreateApiKeyRequest;
import com.hostflow.identity.dto.CreateApiKeyResponse;
import com.hostflow.identity.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@PreAuthorize("hasAuthority('PRODUCT_XANUOS')")
public class ApiKeyController {

    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateApiKeyResponse>> create(@Valid @RequestBody CreateApiKeyRequest request) {
        ApiKeyService.GeneratedKey generated = service.create(request.name());
        CreateApiKeyResponse response = new CreateApiKeyResponse(generated.entity().getId(), generated.entity().getName(),
                generated.rawKey());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(service.list().stream().map(ApiKeyResponse::from).toList()));
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<ApiResponse<Void>> revoke(@PathVariable UUID id) {
        service.revoke(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
