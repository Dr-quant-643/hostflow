package com.hostflow.config.web;

import com.hostflow.config.properties.HostFlowProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Reads allowed origins/methods from HostFlowProperties (application.yml) rather than
 * hardcoding them, so dev/staging/prod can each have different allowed origins
 * without a code change.
 *
 * Disabled in prod: there, browsers only ever talk to gateway-service (which
 * has its own, equivalent CORS config), and app is reached solely through the
 * gateway's internal proxy. Enforcing CORS here too caused two independent
 * checks on the same request -- any drift between the two origin lists (e.g.
 * after a redeploy that updated one but not the other) either duplicated the
 * Access-Control-Allow-Origin header (browsers reject a response with more
 * than one) or hard-rejected the request outright with a 403.
 */
@Configuration
@EnableConfigurationProperties(HostFlowProperties.class)
@Profile("!prod")
public class CorsConfig implements WebMvcConfigurer {

    private final HostFlowProperties properties;

    public CorsConfig(HostFlowProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(properties.getCors().getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(properties.getCors().getAllowedMethods().toArray(new String[0]))
                .allowedHeaders("*")
                .allowCredentials(properties.getCors().isAllowCredentials());
    }
}
