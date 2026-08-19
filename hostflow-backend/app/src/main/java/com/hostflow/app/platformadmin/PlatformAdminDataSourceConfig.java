package com.hostflow.app.platformadmin;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(PlatformAdminDataSourceProperties.class)
public class PlatformAdminDataSourceConfig {

    @Bean(name = "platformAdminDataSource")
    public DataSource platformAdminDataSource(PlatformAdminDataSourceProperties properties) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setMaximumPoolSize(5);
        dataSource.setPoolName("platform-admin-pool");
        return dataSource;
    }

    @Bean(name = "platformAdminJdbcTemplate")
    public JdbcTemplate platformAdminJdbcTemplate(@Qualifier("platformAdminDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
