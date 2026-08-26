package com.hostflow.app.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * THE fix for a real gap: V28's refresh_all_analytics_views() Postgres function
 * (which REFRESHes mv_property_occupancy_summary, mv_monthly_revenue_summary,
 * and mv_booking_analytics) was defined but never called from anywhere --
 * AnalyticsService's dashboard numbers were frozen at whatever they were the
 * moment each view was first created, not live data. Runs cross-tenant (the
 * function refreshes all tenants' rows in one pass; there's no per-tenant
 * variant), same platformAdminJdbcTemplate usage as other scheduled jobs.
 */
@Component
public class RefreshAnalyticsViewsJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshAnalyticsViewsJob.class);

    private final JdbcTemplate platformAdminJdbcTemplate;

    public RefreshAnalyticsViewsJob(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void refresh() {
        try {
            platformAdminJdbcTemplate.execute("SELECT refresh_all_analytics_views()");
        } catch (Exception e) {
            log.warn("Failed to refresh analytics materialized views: {}", e.getMessage());
        }
    }
}
