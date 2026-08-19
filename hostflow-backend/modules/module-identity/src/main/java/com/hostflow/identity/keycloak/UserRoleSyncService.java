package com.hostflow.identity.keycloak;

import com.hostflow.identity.entity.UserRole;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps Keycloak's realm role assignment in sync with Postgres's User.roles after
 * a PATCH roles call. Only ever adds/removes roles that correspond to a known
 * UserRole enum value — any OTHER realm role a user might hold (there are none
 * currently, but this is defensive) is left untouched, so this sync can never
 * accidentally strip an unrelated role assignment.
 */
@Service
public class UserRoleSyncService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakAdminProperties properties;

    public UserRoleSyncService(Keycloak keycloakAdminClient, KeycloakAdminProperties properties) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.properties = properties;
    }

    public void syncRoles(String keycloakId, Set<UserRole> desiredRoles) {
        var userResource = keycloakAdminClient.realm(properties.getRealm()).users().get(keycloakId);
        var realmResource = keycloakAdminClient.realm(properties.getRealm());

        Set<String> knownRoleNames = Arrays.stream(UserRole.values())
                .map(r -> r.name().toLowerCase())
                .collect(Collectors.toSet());

        List<RoleRepresentation> currentlyAssigned = userResource.roles().realmLevel().listAll();
        Set<String> currentKnownRoleNames = currentlyAssigned.stream()
                .map(RoleRepresentation::getName)
                .filter(knownRoleNames::contains)
                .collect(Collectors.toSet());

        Set<String> desiredRoleNames = desiredRoles.stream()
                .map(r -> r.name().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> toAdd = new HashSet<>(desiredRoleNames);
        toAdd.removeAll(currentKnownRoleNames);

        Set<String> toRemove = new HashSet<>(currentKnownRoleNames);
        toRemove.removeAll(desiredRoleNames);

        if (!toAdd.isEmpty()) {
            List<RoleRepresentation> rolesToAdd = toAdd.stream()
                    .map(name -> realmResource.roles().get(name).toRepresentation())
                    .toList();
            userResource.roles().realmLevel().add(rolesToAdd);
        }

        if (!toRemove.isEmpty()) {
            List<RoleRepresentation> rolesToRemove = toRemove.stream()
                    .map(name -> realmResource.roles().get(name).toRepresentation())
                    .toList();
            userResource.roles().realmLevel().remove(rolesToRemove);
        }
    }
}
