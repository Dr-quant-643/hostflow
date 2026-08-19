package com.hostflow.tenancy.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hostflow.tenancy", "com.hostflow.persistence" })
@EntityScan(basePackages = { "com.hostflow.tenancy.entity", "com.hostflow.persistence.entity" })
@EnableJpaRepositories(basePackages = { "com.hostflow.tenancy.repository", "com.hostflow.persistence.repository" })
@ActiveProfiles("test")
public class TestTenancyConfig {
    // Test configuration for tenancy module
}
