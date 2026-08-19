package com.hostflow.identity.keycloak;

import com.hostflow.identity.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleSyncServiceTest {

    @Mock
    private Keycloak keycloak;
    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;
    @Mock
    private UserResource userResource;
    @Mock
    private RoleMappingResource roleMappingResource;
    @Mock
    private RoleScopeResource roleScopeResource;
    @Mock
    private RolesResource rolesResource;
    @Mock
    private RoleResource roleResource;

    private KeycloakAdminProperties properties;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        properties = new KeycloakAdminProperties();
        properties.setRealm("hostflow");

        when(keycloak.realm("hostflow")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(anyString())).thenReturn(roleResource);
    }

    @Test
    void syncRoles_addsNewRole_whenNotCurrentlyAssigned() {
        when(roleScopeResource.listAll()).thenReturn(List.of());
        RoleRepresentation managerRole = new RoleRepresentation();
        managerRole.setName("xanuos_manager");
        when(roleResource.toRepresentation()).thenReturn(managerRole);

        UserRoleSyncService service = new UserRoleSyncService(keycloak, properties);
        service.syncRoles("kc-1", Set.of(UserRole.XANUOS_MANAGER));

        verify(roleScopeResource).add(anyList());
        verify(roleScopeResource, never()).remove(anyList());
    }

    @Test
    void syncRoles_removesRole_whenNoLongerDesired() {
        RoleRepresentation staffRole = new RoleRepresentation();
        staffRole.setName("xanuos_staff");
        when(roleScopeResource.listAll()).thenReturn(List.of(staffRole));
        when(roleResource.toRepresentation()).thenReturn(staffRole);

        UserRoleSyncService service = new UserRoleSyncService(keycloak, properties);
        service.syncRoles("kc-1", Set.of());

        verify(roleScopeResource).remove(anyList());
        verify(roleScopeResource, never()).add(anyList());
    }
}
