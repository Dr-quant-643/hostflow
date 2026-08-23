package com.hostflow.app.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * THE FIX for a real, live cross-tenant RLS bypass: Spring Boot's own
 * DataSourceAutoConfiguration is annotated @ConditionalOnMissingBean(DataSource.class),
 * so as soon as PlatformAdminDataSourceConfig registered its explicit
 * "platformAdminDataSource" bean, Spring Boot's auto-configured datasource for
 * spring.datasource.* (the hostflow_app tenant-scoped role) was never created at
 * all — platformAdminDataSource (hostflow_platform_admin, BYPASSRLS) became the
 * ONLY DataSource bean in the entire application context, so EVERY JPA repository
 * in EVERY module silently ran with full RLS-bypass privileges. This was caught
 * via a live functional audit: one tenant's GET /properties returned another
 * tenant's rows despite SET LOCAL app.current_tenant and the RLS policy both
 * being individually correct — traced to `current_user` on the transaction's
 * actual connection being hostflow_platform_admin, not hostflow_app.
 *
 * Explicitly defining and marking @Primary the real tenant-scoped datasource
 * here restores Spring Boot's normal spring.datasource.* + spring.datasource.hikari.*
 * binding (via @ConfigurationProperties, the same mechanism DataSourceAutoConfiguration
 * itself uses) and makes it win every by-type injection point unambiguously,
 * including Hibernate's own EntityManagerFactory construction and
 * TenantTransactionManagerConfig's DataSource parameter.
 */
@Configuration
public class PrimaryDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
