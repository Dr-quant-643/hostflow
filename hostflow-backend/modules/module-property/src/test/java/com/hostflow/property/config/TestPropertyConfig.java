package com.hostflow.property.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.hostflow.property.entity",
    "com.hostflow.persistence.entity",
    "com.hostflow.tenancy.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.hostflow.property.repository",
    "com.hostflow.persistence.repository"
})
@ActiveProfiles("test")
public class TestPropertyConfig {
    // Test configuration for property module
}
