package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.dto.OrgUserSummaryResponse;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.entity.UserRole;
import com.hostflow.identity.keycloak.UserRoleSyncService;
import com.hostflow.identity.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * Unlike OrgUserAdminService (platform-admin, cross-tenant, explicit
 * TenantContext
 * management), this service runs under the CALLER'S OWN tenant context — set
 * naturally by JwtTenantResolvingFilter from the owner/manager's own JWT
 * tenant_id
 * claim, same as every other normal RLS-scoped request in the system. No manual
 * TenantContext handling needed here at all; RLS does the isolation.
 *
 * Deliberately prevents an owner/manager from granting PLATFORM_ADMIN via this
 * self-service path — that role can only ever be assigned by an existing
 * platform
 * admin through OrgUserAdminService, closing an obvious privilege-escalation
 * hole.
 */
@Service
public class SelfServiceUserAdminService {

    private final UserRepository userRepository;
    private final UserRoleSyncService roleSyncService;

    public SelfServiceUserAdminService(UserRepository userRepository, UserRoleSyncService roleSyncService) {
        this.userRepository = userRepository;
        this.roleSyncService = roleSyncService;
    }

    public Page<OrgUserSummaryResponse> listMyOrgUsers(int limit, int offset) {
        return userRepository.findAll(PageRequest.of(offset / Math.max(limit, 1), limit))
                .map(OrgUserSummaryResponse::from);
    }

    public Page<OrgUserSummaryResponse> searchMyOrgUsers(String query, int limit, int offset) {
        return userRepository.search(query, PageRequest.of(offset / Math.max(limit, 1), limit))
                .map(OrgUserSummaryResponse::from);
    }

    public OrgUserSummaryResponse updateRoles(UUID userId, UpdateUserRolesRequest request) {
        if (request.roles().contains(UserRole.PLATFORM_ADMIN)) {
            throw new BusinessRuleException(
                    "PLATFORM_ADMIN cannot be granted through self-service org management");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.replaceRoles(request.roles());
        user = userRepository.save(user);

        roleSyncService.syncRoles(user.getKeycloakId(), request.roles());

        return OrgUserSummaryResponse.from(user);
    }

    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.deactivate();
        userRepository.save(user);
    }
}
