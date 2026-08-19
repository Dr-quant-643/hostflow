package com.hostflow.identity.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.entity.UserRole;
import com.hostflow.identity.keycloak.UserRoleSyncService;
import com.hostflow.identity.repository.UserRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgUserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleSyncService roleSyncService;

    private OrgUserAdminService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new OrgUserAdminService(userRepository, roleSyncService);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void listUsers_setsTenantContextToOrgId_thenClearsAfter() {
        UUID orgId = UUID.randomUUID();
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.listUsers(orgId, 20, 0);

        assertThat(TenantContext.isSet()).isFalse();
    }

    @Test
    void updateRoles_throwsResourceNotFound_whenUserMissing() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateRoles(orgId, userId, new UpdateUserRolesRequest(Set.of(UserRole.XANUOS_STAFF))))
                .isInstanceOf(ResourceNotFoundException.class);

        assertThat(TenantContext.isSet()).isFalse();
    }

    @Test
    void updateRoles_savesUpdatedRoles_thenSyncsToKeycloak() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User("kc-1", "staff@example.com", "A", "B", Set.of(UserRole.XANUOS_STAFF));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateRoles(orgId, userId, new UpdateUserRolesRequest(Set.of(UserRole.XANUOS_MANAGER)));

        assertThat(user.getRoles()).containsExactly(UserRole.XANUOS_MANAGER);
        verify(roleSyncService).syncRoles("kc-1", Set.of(UserRole.XANUOS_MANAGER));
        assertThat(TenantContext.isSet()).isFalse();
    }
}
