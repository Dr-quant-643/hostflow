package com.hostflow.app.scheduling;

import com.hostflow.booking.entity.Booking;
import com.hostflow.booking.messaging.BookingEventPublisher;
import com.hostflow.booking.repository.BookingRepository;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Closes the hostflow.booking.expired gap — previously declared with no
 * producer. A PENDING booking left unconfirmed for 24h is auto-cancelled and an
 * "expired" event fires. Uses the same cross-tenant-scan pattern as
 * OverdueInvoiceSweepJob (platformAdminJdbcTemplate for the scan), then switches
 * to the normal RLS-scoped BookingRepository (with TenantContext set per row) for
 * the actual write, so Booking's cancel() business-rule guard still runs.
 */
@Component
public class ExpireStalePendingBookingsJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireStalePendingBookingsJob.class);

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final BookingRepository bookingRepository;
    private final BookingEventPublisher eventPublisher;

    public ExpireStalePendingBookingsJob(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
                                          BookingRepository bookingRepository,
                                          BookingEventPublisher eventPublisher) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.bookingRepository = bookingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void expireStaleBookings() {
        List<UUID> staleIds = platformAdminJdbcTemplate.query(
                "SELECT id FROM bookings WHERE status = 'PENDING' AND created_at < now() - INTERVAL '24 hours'",
                (rs, rowNum) -> UUID.fromString(rs.getString("id")));

        int expiredCount = 0;
        for (UUID bookingId : staleIds) {
            UUID tenantId = platformAdminJdbcTemplate.queryForObject(
                    "SELECT tenant_id FROM bookings WHERE id = ?", (rs, rowNum) -> UUID.fromString(rs.getString(1)), bookingId);

            TenantContext.set(tenantId);
            try {
                Booking booking = bookingRepository.findById(bookingId).orElse(null);
                if (booking != null) {
                    booking.cancel();
                    eventPublisher.expired(booking);
                    expiredCount++;
                }
            } finally {
                TenantContext.clear();
            }
        }
        if (expiredCount > 0) {
            log.info("Expired {} stale PENDING booking(s)", expiredCount);
        }
    }
}
