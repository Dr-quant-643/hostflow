package com.hostflow.app.scheduling;

import com.hostflow.booking.entity.ExternalCalendarLink;
import com.hostflow.booking.repository.ExternalCalendarLinkRepository;
import com.hostflow.booking.service.ExternalCalendarBlockService;
import com.hostflow.booking.service.ExternalCalendarBlockService.ParsedEvent;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Polls every owner-configured ExternalCalendarLink's .ics feed URL and
 * turns its VEVENTs into ExternalCalendarBlock rows -- this is what actually
 * makes the free iCal channel-sync feature work; without this job, links
 * would just sit there unread. Deliberately a hand-rolled minimal parser
 * (UID/DTSTART/DTEND only, no RRULE/timezone handling) rather than a new
 * Maven dependency -- OTA availability-block exports (Airbnb, Booking.com,
 * VRBO) are simple non-recurring all-day VEVENTs, which is the one shape
 * this needs to handle correctly.
 *
 * Uses JDK's built-in java.net.http.HttpClient -- no new dependency, no
 * external cost, consistent with every other feature added this round.
 */
@Component
public class ExternalCalendarSyncJob {

    private static final Logger log = LoggerFactory.getLogger(ExternalCalendarSyncJob.class);
    private static final DateTimeFormatter ICS_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Pattern UID_PATTERN = Pattern.compile("^UID:(.+)$", Pattern.MULTILINE);
    private static final Pattern DTSTART_PATTERN = Pattern.compile("^DTSTART[^:]*:(\\d{8})", Pattern.MULTILINE);
    private static final Pattern DTEND_PATTERN = Pattern.compile("^DTEND[^:]*:(\\d{8})", Pattern.MULTILINE);

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final ExternalCalendarLinkRepository linkRepository;
    private final ExternalCalendarBlockService blockService;
    private final HttpClient httpClient;

    public ExternalCalendarSyncJob(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
            ExternalCalendarLinkRepository linkRepository, ExternalCalendarBlockService blockService) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.linkRepository = linkRepository;
        this.blockService = blockService;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Scheduled(cron = "0 0 */3 * * *")
    public void syncAll() {
        List<Object[]> links = platformAdminJdbcTemplate.query(
                "SELECT id, tenant_id FROM external_calendar_links",
                (rs, rowNum) -> new Object[] { UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("tenant_id")) });

        int synced = 0;
        int failed = 0;
        for (Object[] row : links) {
            UUID linkId = (UUID) row[0];
            UUID tenantId = (UUID) row[1];
            TenantContext.set(tenantId);
            try {
                if (syncOne(linkId)) {
                    synced++;
                } else {
                    failed++;
                }
            } finally {
                TenantContext.clear();
            }
        }
        if (synced > 0 || failed > 0) {
            log.info("External calendar sync: {} succeeded, {} failed", synced, failed);
        }
    }

    private boolean syncOne(UUID linkId) {
        ExternalCalendarLink link = linkRepository.findById(linkId).orElse(null);
        if (link == null) {
            return false;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(link.getIcsUrl()))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                link.markSyncFailed("HTTP " + response.statusCode());
                linkRepository.save(link);
                return false;
            }
            List<ParsedEvent> events = parseEvents(response.body());
            blockService.syncBlocks(linkId, link.getPropertyId(), events);
            link.markSynced();
            linkRepository.save(link);
            return true;
        } catch (Exception e) {
            log.warn("Failed to sync external calendar link {}: {}", linkId, e.getMessage());
            link.markSyncFailed(e.getMessage());
            linkRepository.save(link);
            return false;
        }
    }

    /** Splits on VEVENT boundaries, then pulls UID/DTSTART/DTEND out of each
     *  block. A VEVENT missing any of the three is skipped rather than
     *  failing the whole feed -- a malformed one-off event shouldn't block
     *  every other real block in the same calendar. */
    private List<ParsedEvent> parseEvents(String icsBody) {
        List<ParsedEvent> events = new ArrayList<>();
        String normalized = icsBody.replace("\r\n", "\n").replace("\r", "\n");
        int index = 0;
        while (true) {
            int start = normalized.indexOf("BEGIN:VEVENT", index);
            if (start == -1) break;
            int end = normalized.indexOf("END:VEVENT", start);
            if (end == -1) break;
            String block = normalized.substring(start, end);
            index = end + "END:VEVENT".length();

            ParsedEvent event = parseOneEvent(block);
            if (event != null) {
                events.add(event);
            }
        }
        return events;
    }

    private ParsedEvent parseOneEvent(String block) {
        Matcher uidMatcher = UID_PATTERN.matcher(block);
        Matcher startMatcher = DTSTART_PATTERN.matcher(block);
        Matcher endMatcher = DTEND_PATTERN.matcher(block);
        if (!uidMatcher.find() || !startMatcher.find() || !endMatcher.find()) {
            return null;
        }
        try {
            String uid = uidMatcher.group(1).trim();
            LocalDate startDate = LocalDate.parse(startMatcher.group(1), ICS_DATE);
            LocalDate endDate = LocalDate.parse(endMatcher.group(1), ICS_DATE);
            if (!endDate.isAfter(startDate)) {
                return null;
            }
            return new ParsedEvent(uid, startDate, endDate);
        } catch (Exception e) {
            return null;
        }
    }
}
