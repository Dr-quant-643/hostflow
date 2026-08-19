package com.hostflow.platformadmin.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.platformadmin.dto.AuditLogResponse;
import com.hostflow.platformadmin.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/audit-log")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> list(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Page<AuditLogResponse> page = (tenantId != null
                ? service.listByTenant(tenantId, limit, offset)
                : service.listAll(limit, offset)).map(AuditLogResponse::from);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
