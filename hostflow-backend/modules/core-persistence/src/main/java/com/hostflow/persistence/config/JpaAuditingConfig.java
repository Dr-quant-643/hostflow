package com.hostflow.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Activates @CreatedDate / @LastModifiedDate handling declared on BaseEntity.
 * Without this, those annotations are silently ignored and timestamps stay null.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
