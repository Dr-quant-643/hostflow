package com.hostflow.identity.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.dto.OrgUserSummaryResponse;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.keycloak.UserRoleSyncService;
import com.hostflow.identity.repository.UserRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Serves the PLATFORM_ADMIN-only org-user-management screens (xanuos-console).
 * The caller (a platform admin) has NO tenant of their own — the org being managed
 * is specified explicitly via orgId in every method here, and TenantContext is set
 * to that value for the duration of each call, then cleared. Because UserRepository
 * is itself a proxied Spring Data JPA bean (not a self-invoked method on this
 * class), each repository call runs in its own real transaction — the
 * self-invocation trap from module-billing's InvoiceService does NOT apply here,
 * since there's no @Transactional method on THIS class being called via `this.`.
 */
@Service
public class OrgUserAdminService {

    private final UserRepository userRepository;
    private final UserRoleSyncService roleSyncService;

    public OrgUserAdminService(UserRepository userRepository, UserRoleSyncService roleSyncService) {
        this.userRepository = userRepository;
        this.roleSyncService = roleSyncService;
    }

    public List<OrgUserSummaryResponse> listUsers(UUID orgId, int limit, int offset) {
        TenantContext.set(orgId);
        try {
            return userRepository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit))
                    .stream().map(OrgUserSummaryResponse::from).toList();
        } finally {
            TenantContext.clear();
        }
    }

    public List<OrgUserSummaryResponse> searchUsers(UUID orgId, String query, int limit, int offset) {
        TenantContext.set(orgId);
        try {
            return userRepository.search(query, PageRequest.of(offset / Math.max(limit, 1), limit))
                    .stream().map(OrgUserSummaryResponse::from).toList();
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Order: Postgres updated first, then Keycloak synced. Documented limitation
     * (consistent with how known gaps are handled throughout this codebase): if the
     * Keycloak sync step fails after the Postgres save succeeds, the two systems
     * are left inconsistent until a retry. Not fully atomic across the two systems
     * — acceptable for Phase 1/2, flagged as a follow-up rather than silently
     * assumed safe.
     */
    public OrgUserSummaryResponse updateRoles(UUID orgId, UUID userId, UpdateUserRolesRequest request) {
        TenantContext.set(orgId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));

            user.replaceRoles(request.roles());
            user = userRepository.save(user);

            roleSyncService.syncRoles(user.getKeycloakId(), request.roles());

            return OrgUserSummaryResponse.from(user);
        } finally {
            TenantContext.clear();
        }
    }
}
