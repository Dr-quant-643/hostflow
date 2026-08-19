package com.hostflow.identity.service;

import com.hostflow.common.exception.BusinessRuleException;
import com.hostflow.identity.dto.UpdateUserRolesRequest;
import com.hostflow.identity.entity.User;
import com.hostflow.identity.entity.UserRole;
import com.hostflow.identity.keycloak.UserRoleSyncService;
import com.hostflow.identity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelfServiceUserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleSyncService roleSyncService;

    private SelfServiceUserAdminService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new SelfServiceUserAdminService(userRepository, roleSyncService);
    }

    @Test
    void updateRoles_rejectsGrantingPlatformAdmin() {
        UUID userId = UUID.randomUUID();
        UpdateUserRolesRequest request = new UpdateUserRolesRequest(Set.of(UserRole.PLATFORM_ADMIN));

        assertThatThrownBy(() -> service.updateRoles(userId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("self-service");
    }

    @Test
    void updateRoles_allowsLegitimateRoleChange() {
        UUID userId = UUID.randomUUID();
        User user = new User("kc-1", "staff@example.com", "A", "B", Set.of(UserRole.XANUOS_STAFF));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateRoles(userId, new UpdateUserRolesRequest(Set.of(UserRole.XANUOS_MANAGER)));

        assertThat(user.getRoles()).containsExactly(UserRole.XANUOS_MANAGER);
    }

    @Test
    void deactivateUser_setsActiveFalse() {
        UUID userId = UUID.randomUUID();
        User user = new User("kc-1", "staff@example.com", "A", "B", Set.of(UserRole.XANUOS_STAFF));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.deactivateUser(userId);

        assertThat(user.isActive()).isFalse();
    }
}
