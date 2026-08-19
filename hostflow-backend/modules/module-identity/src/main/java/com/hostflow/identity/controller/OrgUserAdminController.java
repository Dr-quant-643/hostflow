package com.hostflow.identity.controller;

import com.hostflow.common.response.ApiResponse;
import com.hostflow.identity.dto.OrgUserSummaryResponse;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.service.OrgUserAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{orgId}/users")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class OrgUserAdminController {

    private final OrgUserAdminService orgUserAdminService;

    public OrgUserAdminController(OrgUserAdminService orgUserAdminService) {
        this.orgUserAdminService = orgUserAdminService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrgUserSummaryResponse>>> list(
            @PathVariable UUID orgId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(orgUserAdminService.listUsers(orgId, limit, offset)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<OrgUserSummaryResponse>>> search(
            @PathVariable UUID orgId,
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(ApiResponse.success(orgUserAdminService.searchUsers(orgId, q, limit, offset)));
    }

    @PatchMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<OrgUserSummaryResponse>> updateRoles(
            @PathVariable UUID orgId, @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orgUserAdminService.updateRoles(orgId, userId, request)));
    }
}
