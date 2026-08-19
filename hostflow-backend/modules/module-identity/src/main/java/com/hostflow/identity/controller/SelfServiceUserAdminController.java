package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.OrgUserSummaryResponse;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.service.SelfServiceUserAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * The self-service counterpart to OrgUserAdminController. Restricted to
 * ROLE_XANUOS_OWNER or ROLE_XANUOS_MANAGER specifically — a XANUOS_STAFF member
 * (holding only PRODUCT_XANUOS) cannot manage other staff, only owners/managers
 * can, matching normal org-hierarchy expectations.
 */
@RestController
@RequestMapping("/api/v1/my-organization/users")
@PreAuthorize("hasAnyRole('XANUOS_OWNER', 'XANUOS_MANAGER')")
public class SelfServiceUserAdminController {

    private final SelfServiceUserAdminService service;

    public SelfServiceUserAdminController(SelfServiceUserAdminService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrgUserSummaryResponse>>> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(service.listMyOrgUsers(limit, offset)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<OrgUserSummaryResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(service.searchMyOrgUsers(q, limit, offset)));
    }

    @PatchMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<OrgUserSummaryResponse>> updateRoles(
            @PathVariable UUID userId, @Valid @RequestBody UpdateUserRolesRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateRoles(userId, request)));
    }

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID userId) {
        service.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
