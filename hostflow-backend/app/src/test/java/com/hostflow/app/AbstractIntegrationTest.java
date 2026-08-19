package com.hostflow.app;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for every real-infrastructure integration test in this module. Spins
 * up REAL Postgres (with pgvector — note the custom image tag below) and REAL
 * RabbitMQ containers, exactly once per test class (STATIC @Container fields are
 * shared across all test methods in a subclass for speed). This is where every
 * module's deferred "full Postgres/RLS/Flyway integration test" and "full
 * RabbitMQ publish-consume test" finally happen for real, closing that standing
 * open item repeated in nearly every module report since core-persistence.
 */
@SpringBootTest
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
            .withDatabaseName("hostflow_test")
            .withUsername("hostflow_migrations")
            .withPassword("test_password");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        // Testcontainers' Postgres user IS the superuser in this container, so it
        // already has CREATEROLE — satisfies core-persistence's V2 migration note
        // about needing CREATEROLE privilege the first time roles are created.
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "http://localhost:8081/realms/hostflow-test");
    }
}
