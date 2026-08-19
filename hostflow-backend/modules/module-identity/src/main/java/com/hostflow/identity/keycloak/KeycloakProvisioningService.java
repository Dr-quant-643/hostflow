package com.hostflow.identity.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * THIS closes open item #10 from core-security's report: automates setting
 * tenant_id and product_scope as Keycloak user attributes at creation time, via the
 * Admin REST API, instead of a manual admin-console step for every user.
 *
 * Flow: create the Keycloak user (disabled initially) -> set tenant_id/product_scope
 * attributes -> assign realm roles -> trigger a "verify email / set password" action
 * email via Keycloak -> enable the user. If any step fails after the user record is
 * created, provisioning is rolled back by deleting the partially-created Keycloak user,
 * so a failed provisioning never leaves an orphaned, half-configured account.
 */
@Service
public class KeycloakProvisioningService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakAdminProperties properties;

    public KeycloakProvisioningService(Keycloak keycloakAdminClient, KeycloakAdminProperties properties) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.properties = properties;
    }

    public String provisionUser(UUID tenantId, String email, String firstName, String lastName,
                                 List<String> realmRoles, List<String> productScopes) {
        UserRepresentation representation = new UserRepresentation();
        representation.setUsername(email);
        representation.setEmail(email);
        representation.setFirstName(firstName);
        representation.setLastName(lastName);
        representation.setEnabled(false);
        representation.setEmailVerified(false);
        representation.setAttributes(Map.of(
                "tenant_id", List.of(tenantId.toString()),
                "product_scope", productScopes
        ));

        var usersResource = keycloakAdminClient.realm(properties.getRealm()).users();
        String keycloakUserId;

        try (Response response = usersResource.create(representation)) {
            if (response.getStatus() != 201) {
                throw new KeycloakProvisioningException(
                        "Keycloak user creation failed with status " + response.getStatus(), null);
            }
            keycloakUserId = extractIdFromLocationHeader(response);
        } catch (Exception e) {
            throw new KeycloakProvisioningException("Failed to create Keycloak user for " + email, e);
        }

        try {
            assignRealmRoles(keycloakUserId, realmRoles);
            // Keycloak rejects execute-actions-email for a disabled user ("User is
            // disabled", 400) -- must enable before sending, not after.
            usersResource.get(keycloakUserId).update(enabledCopy(representation));
            sendRequiredActionsEmail(keycloakUserId);
        } catch (Exception e) {
            // Rollback: never leave a half-provisioned Keycloak user behind.
            usersResource.get(keycloakUserId).remove();
            throw new KeycloakProvisioningException(
                    "Provisioning failed after user creation for " + email + "; rolled back", e);
        }

        return keycloakUserId;
    }

    private UserRepresentation enabledCopy(UserRepresentation representation) {
        representation.setEnabled(true);
        return representation;
    }

    private void assignRealmRoles(String keycloakUserId, List<String> realmRoles) {
        var realmResource = keycloakAdminClient.realm(properties.getRealm());
        var roleRepresentations = realmRoles.stream()
                .map(role -> realmResource.roles().get(role).toRepresentation())
                .toList();
        realmResource.users().get(keycloakUserId).roles().realmLevel().add(roleRepresentations);
    }

    private void sendRequiredActionsEmail(String keycloakUserId) {
        keycloakAdminClient.realm(properties.getRealm())
                .users()
                .get(keycloakUserId)
                .executeActionsEmail(List.of("UPDATE_PASSWORD", "VERIFY_EMAIL"));
    }

    private String extractIdFromLocationHeader(Response response) {
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
