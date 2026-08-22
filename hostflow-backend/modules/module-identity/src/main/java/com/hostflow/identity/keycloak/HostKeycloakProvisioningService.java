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
 * Self-service counterpart to KeycloakProvisioningService for the one case
 * where an owner provisions themselves at signup instead of being invited by
 * an existing admin: sets the password directly and enables the user
 * immediately, same reasoning as GuestKeycloakProvisioningService (no admin
 * in the loop, no SMTP configured, so an invite-email flow would never be
 * seen). Unlike a guest, a host DOES get tenant_id set -- XanuOS entities are
 * tenant-scoped, and JwtTenantResolvingFilter needs that claim to resolve
 * row-level security for this user's requests.
 */
@Service
public class HostKeycloakProvisioningService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakAdminProperties properties;

    public HostKeycloakProvisioningService(Keycloak keycloakAdminClient, KeycloakAdminProperties properties) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.properties = properties;
    }

    public String provisionHostOwner(UUID tenantId, String email, String firstName, String lastName, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation representation = new UserRepresentation();
        representation.setUsername(email);
        representation.setEmail(email);
        representation.setFirstName(firstName);
        representation.setLastName(lastName);
        representation.setEnabled(true);
        representation.setEmailVerified(false);
        representation.setAttributes(Map.of(
                "tenant_id", List.of(tenantId.toString()),
                "product_scope", List.of("XANUOS")
        ));
        representation.setCredentials(List.of(credential));

        var usersResource = keycloakAdminClient.realm(properties.getRealm()).users();
        String keycloakUserId;

        try (Response response = usersResource.create(representation)) {
            if (response.getStatus() != 201) {
                throw new KeycloakProvisioningException(
                        "Keycloak host user creation failed with status " + response.getStatus(), null);
            }
            keycloakUserId = extractIdFromLocationHeader(response);
        } catch (Exception e) {
            throw new KeycloakProvisioningException("Failed to create Keycloak host user for " + email, e);
        }

        try {
            var realmResource = keycloakAdminClient.realm(properties.getRealm());
            var role = realmResource.roles().get("xanuos_owner").toRepresentation();
            realmResource.users().get(keycloakUserId).roles().realmLevel().add(List.of(role));
        } catch (Exception e) {
            usersResource.get(keycloakUserId).remove();
            throw new KeycloakProvisioningException(
                    "Host provisioning failed after user creation for " + email + "; rolled back", e);
        }

        return keycloakUserId;
    }

    private String extractIdFromLocationHeader(Response response) {
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
