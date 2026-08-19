package com.hostflow.persistence.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.hostflow.persistence.entity")
public class TestJpaConfig {
    // Minimal test configuration — core-persistence is a plain library module
    // with no Spring Boot application class of its own, so @DataJpaTest has
    // nothing to auto-detect without this, same pattern as module-identity's
    // TestJpaConfig.
}
