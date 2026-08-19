package com.hostflow.app.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AnalyticsRefreshJob {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Refresh analytics materialized views daily at 2 AM
     * Uses CONCURRENTLY to avoid locking views
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshAnalytics() {
        log.info("Starting analytics refresh at {}", LocalDateTime.now());
        try {
            jdbcTemplate.execute("SELECT refresh_analytics_views()");
            log.info("Analytics views refreshed successfully at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to refresh analytics views: {}", e.getMessage(), e);
        }
    }

    /**
     * Also refresh on startup (optional)
     * Uncomment if you want fresh data on app start
     */
    // @PostConstruct
    // public void refreshOnStartup() {
    //     log.info("Refreshing analytics views on startup...");
    //     refreshAnalytics();
    // }

    /**
     * Manual trigger for testing
     * Can be called via JMX or REST endpoint
     */
    public void refreshManually() {
        log.info("Manual analytics refresh triggered");
        refreshAnalytics();
    }
}
