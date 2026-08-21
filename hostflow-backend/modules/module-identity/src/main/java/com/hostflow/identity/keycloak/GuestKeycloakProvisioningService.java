package com.hostflow.identity.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Deliberately separate from KeycloakProvisioningService (which requires a
 * tenantId parameter and always sets the tenant_id attribute). This class sets
 * ONLY product_scope=[NAZILCO] and explicitly NEVER sets tenant_id — the
 * absence
 * of that claim is what JwtTenantResolvingFilter and TenantContext correctly
 * interpret as "no tenant," which is exactly right for a marketplace guest.
 * Assigns the nazilco_customer realm role only.
 *
 * Sets the password directly at creation rather than the invite-email flow
 * used by KeycloakProvisioningService (owner accounts) — self-service guest
 * signup has no admin in the loop to depend on email delivery, and this
 * deployment has no SMTP configured, so an email link would never arrive.
 */
@Service
public class GuestKeycloakProvisioningService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakAdminProperties properties;

    public GuestKeycloakProvisioningService(Keycloak keycloakAdminClient, KeycloakAdminProperties properties) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.properties = properties;
    }

    public String provisionGuest(String email, String firstName, String lastName, String password) {
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
        representation.setAttributes(Map.of("product_scope", List.of("NAZILCO")));
        representation.setCredentials(List.of(credential));

        var usersResource = keycloakAdminClient.realm(properties.getRealm()).users();
        String keycloakUserId;

        try (Response response = usersResource.create(representation)) {
            if (response.getStatus() != 201) {
                throw new KeycloakProvisioningException(
                        "Keycloak guest user creation failed with status " + response.getStatus(), null);
            }
            keycloakUserId = extractIdFromLocationHeader(response);
        } catch (Exception e) {
            throw new KeycloakProvisioningException("Failed to create Keycloak guest user for " + email, e);
        }

        try {
            var realmResource = keycloakAdminClient.realm(properties.getRealm());
            var role = realmResource.roles().get("nazilco_customer").toRepresentation();
            realmResource.users().get(keycloakUserId).roles().realmLevel().add(List.of(role));
        } catch (Exception e) {
            usersResource.get(keycloakUserId).remove();
            throw new KeycloakProvisioningException(
                    "Guest provisioning failed after user creation for " + email + "; rolled back", e);
        }

        return keycloakUserId;
    }

    private String extractIdFromLocationHeader(Response response) {
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
