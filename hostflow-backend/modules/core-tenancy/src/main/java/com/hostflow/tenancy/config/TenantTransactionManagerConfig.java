package com.hostflow.tenancy.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Replaces Spring Boot's default JpaTransactionManager with TenantAwareJpaTransactionManager
 * everywhere in the application. Every @Transactional method in every future module
 * automatically gets tenant-scoped SET LOCAL applied — no per-module opt-in required.
 */
@Configuration
public class TenantTransactionManagerConfig {

    @Bean
    @Primary
    public TenantAwareJpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory,
                                                                 DataSource dataSource) {
        TenantAwareJpaTransactionManager transactionManager = new TenantAwareJpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        // Required so resolveCurrentConnection() above can find the JDBC connection via
        // TransactionSynchronizationManager — without this, JpaTransactionManager doesn't
        // register a ConnectionHolder against the raw DataSource.
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
