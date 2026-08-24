package com.hostflow.identity.keycloak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HostKeycloakProvisioningServiceTest {

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

    @BeforeEach
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
        RoleRepresentation ownerRole = new RoleRepresentation();
        ownerRole.setName("xanuos_owner");
        when(roleResource.toRepresentation()).thenReturn(ownerRole);
    }

    /**
     * Regression test mirroring GuestKeycloakProvisioningServiceTest: the same
     * hard-overwrite bug existed here in reverse -- a NazilCo guest becoming a
     * XanuOS owner on the same Keycloak identity must end up with both
     * product_scope entries, not lose NAZILCO.
     */
    @Test
    void attachExistingUserToOrganization_preservesExistingProductScope() {
        UserRepresentation existing = new UserRepresentation();
        existing.setAttributes(new java.util.HashMap<>(Map.of("product_scope", List.of("NAZILCO"))));
        when(userResource.toRepresentation()).thenReturn(existing);

        HostKeycloakProvisioningService service = new HostKeycloakProvisioningService(keycloak, properties);
        service.attachExistingUserToOrganization("kc-1", UUID.randomUUID());

        ArgumentCaptor<UserRepresentation> captor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userResource).update(captor.capture());
        assertThat(captor.getValue().getAttributes().get("product_scope"))
                .containsExactlyInAnyOrder("NAZILCO", "XANUOS");
    }

    @Test
    void attachExistingUserToOrganization_isIdempotent_whenAlreadyAttached() {
        UserRepresentation existing = new UserRepresentation();
        existing.setAttributes(new java.util.HashMap<>(Map.of("product_scope", List.of("NAZILCO", "XANUOS"))));
        when(userResource.toRepresentation()).thenReturn(existing);

        HostKeycloakProvisioningService service = new HostKeycloakProvisioningService(keycloak, properties);
        service.attachExistingUserToOrganization("kc-1", UUID.randomUUID());

        ArgumentCaptor<UserRepresentation> captor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userResource).update(captor.capture());
        assertThat(captor.getValue().getAttributes().get("product_scope"))
                .containsExactlyInAnyOrder("NAZILCO", "XANUOS");
    }
}
