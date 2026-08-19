package com.hostflow.identity.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds a Keycloak Admin Client authenticated via a dedicated service-account client
 * (client_credentials grant) — NOT the same "hostflow-app" client users log in through.
 * This admin client must be created separately in Keycloak with "Service Accounts
 * Enabled" and the realm-management "manage-users" client role granted to its service
 * account. Credentials come from environment variables, never hardcoded.
 */
@Configuration
@EnableConfigurationProperties(KeycloakAdminProperties.class)
public class KeycloakClientFactory {

    @Bean
    public Keycloak keycloakAdminClient(KeycloakAdminProperties properties) {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getAdminClientId())
                .clientSecret(properties.getAdminClientSecret())
                .grantType("client_credentials")
                .build();
    }
}
