package com.hostflow.config.web;

import com.hostflow.config.properties.HostFlowProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Reads allowed origins/methods from HostFlowProperties (application.yml) rather than
 * hardcoding them, so dev/staging/prod can each have different allowed origins
 * without a code change.
 */
@Configuration
@EnableConfigurationProperties(HostFlowProperties.class)
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
