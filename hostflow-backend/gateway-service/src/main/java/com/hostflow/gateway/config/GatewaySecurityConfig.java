package com.hostflow.gateway.config;

import com.hostflow.gateway.security.GatewayJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * UPDATED: now wires GatewayJwtAuthenticationConverter in explicitly (closing
 * the
 * role-mapping gap flagged while building /admin/health), so
 * hasRole("PLATFORM_ADMIN")
 * on /admin/health actually works correctly against Keycloak's
 * realm_access.roles
 * claim shape, matching core-security's behavior on the Servlet side.
 *
 * UPDATED AGAIN: two gaps found wiring nazilco-web up against a real running
 * backend — (1) no CORS configuration existed anywhere in the gateway, so
 * every direct browser fetch from any of the 5 frontend apps was blocked by
 * the browser before a response ever came back; (2) .anyExchange().authenticated()
 * had no exception for the app's genuinely-anonymous public endpoints
 * (PublicPropertyController, GuestRegistrationController, etc.) — those routes
 * were being rejected with 401 at the gateway, before ever reaching the app's
 * own (correctly-open) @PreAuthorize-free controllers.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class GatewaySecurityConfig {

    // The exact anonymous surface confirmed across module-property, module-booking,
    // module-identity, module-mall, and module-office this phase — nothing else
    // under /api/** should ever bypass authentication at the gateway.
    private static final String[] PUBLIC_API_PATHS = {
            "/api/v1/properties/public/**",
            "/api/v1/bookings/public/availability",
            "/api/v1/guests/register",
            "/api/v1/mall/public/store-directory",
            "/api/v1/office/public/rooms"
    };

    @Value("${hostflow.cors.allowed-origins:http://localhost:3000,http://localhost:3001,http://localhost:3002,http://localhost:3003,http://localhost:3004}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityWebFilterChain gatewayFilterChain(ServerHttpSecurity http,
            GatewayJwtAuthenticationConverter jwtAuthenticationConverter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .pathMatchers("/admin/health").hasRole("PLATFORM_ADMIN")
                        .pathMatchers(PUBLIC_API_PATHS).permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant-ID", "X-Request-ID",
                "Accept", "Origin", "X-CSRF-Token"));
        configuration.setExposedHeaders(List.of("X-Tenant-ID", "X-Request-ID"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
