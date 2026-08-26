package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * The free half of iCal channel sync -- publishes each property's own
 * booked dates as a standard .ics feed an owner can paste into Airbnb/
 * Booking.com/VRBO's "import calendar" field, same as ExternalCalendarLink
 * does the reverse direction. No guest PII in the export (no name/email),
 * only that the dates are blocked -- matches how Airbnb's own exported
 * calendars work.
 */
@RestController
public class IcsExportController {

    private static final DateTimeFormatter ICS_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final JdbcTemplate platformAdminJdbcTemplate;

    public IcsExportController(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    @GetMapping(value = "/api/v1/properties/public/{id}/calendar.ics", produces = "text/calendar")
    public ResponseEntity<String> export(@PathVariable UUID id) {
        String sql = """
                SELECT id, check_in, check_out FROM bookings
                WHERE property_id = ? AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')
                ORDER BY check_in
                """;
        List<String> events = platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> {
            String uid = rs.getString("id");
            Date checkIn = rs.getDate("check_in");
            Date checkOut = rs.getDate("check_out");
            return """
                    BEGIN:VEVENT
                    UID:%s@rvanaflow
                    DTSTART;VALUE=DATE:%s
                    DTEND;VALUE=DATE:%s
                    SUMMARY:Blocked
                    END:VEVENT""".formatted(uid, ICS_DATE.format(checkIn.toLocalDate()), ICS_DATE.format(checkOut.toLocalDate()));
        }, id);

        String calendar = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//RvanaFlow//NazilCo//EN\r\nCALSCALE:GREGORIAN\r\n"
                + String.join("\r\n", events).replace("\n", "\r\n")
                + (events.isEmpty() ? "" : "\r\n") + "END:VCALENDAR\r\n";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"property-" + id + ".ics\"")
                .body(calendar);
    }
}
