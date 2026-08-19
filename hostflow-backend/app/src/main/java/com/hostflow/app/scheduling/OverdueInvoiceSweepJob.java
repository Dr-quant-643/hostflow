package com.hostflow.app.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * FIXED (was Open Item A): previously used the RLS-scoped InvoiceRepository,
 * which
 * requires TenantContext to be set — but scheduled jobs have no HTTP request to
 * set
 * it from, so this job silently processed zero invoices under the old
 * implementation. Now uses the platformAdminJdbcTemplate
 * (hostflow_platform_admin,
 * BYPASSRLS) directly, genuinely scanning across all tenants as intended.
 */
@Component
public class OverdueInvoiceSweepJob {

    private static final Logger log = LoggerFactory.getLogger(OverdueInvoiceSweepJob.class);

    private final JdbcTemplate platformAdminJdbcTemplate;

    public OverdueInvoiceSweepJob(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void sweep() {
        int updated = platformAdminJdbcTemplate.update(
                "UPDATE invoices SET status = 'OVERDUE', updated_at = now() " +
                        "WHERE status = 'ISSUED' AND due_date < CURRENT_DATE");
        log.info("Overdue invoice sweep marked {} invoice(s) as OVERDUE (cross-tenant)", updated);
    }
}
