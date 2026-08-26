package com.hostflow.app.publicapi;

import com.hostflow.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The foundation for the eventual paid public-API product: authenticated by
 * an X-Api-Key header (PublicApiKeyAuth), not the normal JWT flow, so this
 * path is added to SecurityConfig's permitAll list and each method
 * authenticates manually -- same shape as every other cross-tenant lookup
 * in this package, just with an API key standing in for "no JWT tenant to
 * scope by." Deliberately read-only, no rate limiting/usage metering yet
 * (see class doc on ApiKey for why).
 */
@RestController
@RequestMapping("/api/v1/public-api")
public class PublicApiController {

    private final PublicApiKeyAuth keyAuth;
    private final GuestSegmentQueries guestSegmentQueries;
    private final JdbcTemplate platformAdminJdbcTemplate;

    public PublicApiController(PublicApiKeyAuth keyAuth, GuestSegmentQueries guestSegmentQueries,
            @Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.keyAuth = keyAuth;
        this.guestSegmentQueries = guestSegmentQueries;
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record PropertyRow(UUID id, String name, String propertyType, String rentalModel, String city,
            String country, BigDecimal basePrice) {
    }

    @GetMapping("/properties")
    public ResponseEntity<ApiResponse<List<PropertyRow>>> properties(@RequestHeader("X-Api-Key") String apiKey) {
        UUID tenantId = keyAuth.resolveTenant(apiKey);
        List<PropertyRow> rows = platformAdminJdbcTemplate.query(
                "SELECT id, name, property_type, rental_model, city, country, base_price FROM properties "
                        + "WHERE tenant_id = ? AND status = 'ACTIVE' ORDER BY name",
                (rs, rowNum) -> new PropertyRow(UUID.fromString(rs.getString("id")), rs.getString("name"),
                        rs.getString("property_type"), rs.getString("rental_model"), rs.getString("city"),
                        rs.getString("country"), rs.getBigDecimal("base_price")),
                tenantId);
        return ResponseEntity.ok(ApiResponse.success(rows));
    }

    public record BookingRow(UUID id, UUID propertyId, LocalDate checkIn, LocalDate checkOut, String status,
            BigDecimal totalPrice) {
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingRow>>> bookings(@RequestHeader("X-Api-Key") String apiKey) {
        UUID tenantId = keyAuth.resolveTenant(apiKey);
        List<BookingRow> rows = platformAdminJdbcTemplate.query(
                "SELECT id, property_id, check_in, check_out, status, total_price FROM bookings "
                        + "WHERE tenant_id = ? ORDER BY created_at DESC LIMIT 200",
                (rs, rowNum) -> new BookingRow(UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("property_id")),
                        rs.getDate("check_in").toLocalDate(), rs.getDate("check_out").toLocalDate(),
                        rs.getString("status"), rs.getBigDecimal("total_price")),
                tenantId);
        return ResponseEntity.ok(ApiResponse.success(rows));
    }

    @GetMapping("/guest-segments")
    public ResponseEntity<ApiResponse<List<GuestSegmentQueries.GuestSegmentRow>>> guestSegments(
            @RequestHeader("X-Api-Key") String apiKey) {
        UUID tenantId = keyAuth.resolveTenant(apiKey);
        return ResponseEntity.ok(ApiResponse.success(guestSegmentQueries.segmentsForTenant(tenantId)));
    }
}
