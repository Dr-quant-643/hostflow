package com.hostflow.identity.keycloak;

import com.hostflow.common.exception.BusinessRuleException;
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
            if (response.getStatus() == 409) {
                // Most commonly: this email already has a Keycloak identity from
                // "Continue with Google" -- the signup form can't create a second
                // one for the same email/username, and shouldn't try to.
                throw new BusinessRuleException(
                        "An account with email '" + email + "' already exists. If you signed up with Google, "
                                + "use \"Continue with Google\" to log in instead.");
            }
            if (response.getStatus() != 201) {
                throw new KeycloakProvisioningException(
                        "Keycloak host user creation failed with status " + response.getStatus(), null);
            }
            keycloakUserId = extractIdFromLocationHeader(response);
        } catch (BusinessRuleException e) {
            throw e;
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

    /**
     * Attaches an already-authenticated Keycloak identity (e.g. one created via
     * "Continue with Google", which has no tenant_id/product_scope yet) to a
     * newly created organization -- the counterpart to provisionHostOwner()
     * for a user who already has a Keycloak account but no workspace. Merges
     * into the existing attribute map (never overwrites) so provider-supplied
     * attributes like "picture" survive. Also merges product_scope itself: a
     * user who already carries e.g. product_scope=[NAZILCO] (an existing
     * NazilCo guest becoming a XanuOS owner with the same identity) must end
     * up with [NAZILCO, XANUOS], not have NAZILCO silently clobbered.
     */
    public void attachExistingUserToOrganization(String keycloakUserId, UUID tenantId) {
        var usersResource = keycloakAdminClient.realm(properties.getRealm()).users();
        var userResource = usersResource.get(keycloakUserId);
        UserRepresentation representation = userResource.toRepresentation();

        Map<String, List<String>> attributes = representation.getAttributes() != null
                ? new java.util.HashMap<>(representation.getAttributes())
                : new java.util.HashMap<>();
        attributes.put("tenant_id", List.of(tenantId.toString()));
        List<String> existingScopes = attributes.getOrDefault("product_scope", List.of());
        if (!existingScopes.contains("XANUOS")) {
            List<String> mergedScopes = new java.util.ArrayList<>(existingScopes);
            mergedScopes.add("XANUOS");
            attributes.put("product_scope", mergedScopes);
        }
        representation.setAttributes(attributes);
        userResource.update(representation);

        try {
            var realmResource = keycloakAdminClient.realm(properties.getRealm());
            var role = realmResource.roles().get("xanuos_owner").toRepresentation();
            realmResource.users().get(keycloakUserId).roles().realmLevel().add(List.of(role));
        } catch (Exception e) {
            throw new KeycloakProvisioningException(
                    "Failed to assign xanuos_owner role to existing user " + keycloakUserId, e);
        }
    }

    private String extractIdFromLocationHeader(Response response) {
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
