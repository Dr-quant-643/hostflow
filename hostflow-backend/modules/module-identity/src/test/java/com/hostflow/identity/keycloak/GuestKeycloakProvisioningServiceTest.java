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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestKeycloakProvisioningServiceTest {

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
        RoleRepresentation guestRole = new RoleRepresentation();
        guestRole.setName("nazilco_customer");
        when(roleResource.toRepresentation()).thenReturn(guestRole);
    }

    /**
     * Regression test for the bug that broke hostflow01@gmail.com's XanuOS
     * login: attachExistingUserAsGuest used to hard-overwrite product_scope to
     * [NAZILCO], silently destroying an existing [XANUOS] scope on the same
     * Keycloak identity. It must merge instead.
     */
    @Test
    void attachExistingUserAsGuest_preservesExistingProductScope() {
        UserRepresentation existing = new UserRepresentation();
        existing.setAttributes(new java.util.HashMap<>(Map.of("product_scope", List.of("XANUOS"))));
        when(userResource.toRepresentation()).thenReturn(existing);

        GuestKeycloakProvisioningService service = new GuestKeycloakProvisioningService(keycloak, properties);
        service.attachExistingUserAsGuest("kc-1");

        ArgumentCaptor<UserRepresentation> captor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userResource).update(captor.capture());
        assertThat(captor.getValue().getAttributes().get("product_scope"))
                .containsExactlyInAnyOrder("XANUOS", "NAZILCO");
    }

    @Test
    void attachExistingUserAsGuest_isIdempotent_whenAlreadyAttached() {
        UserRepresentation existing = new UserRepresentation();
        existing.setAttributes(new java.util.HashMap<>(Map.of("product_scope", List.of("XANUOS", "NAZILCO"))));
        when(userResource.toRepresentation()).thenReturn(existing);

        GuestKeycloakProvisioningService service = new GuestKeycloakProvisioningService(keycloak, properties);
        service.attachExistingUserAsGuest("kc-1");

        ArgumentCaptor<UserRepresentation> captor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userResource).update(captor.capture());
        assertThat(captor.getValue().getAttributes().get("product_scope"))
                .containsExactlyInAnyOrder("XANUOS", "NAZILCO");
    }
}
