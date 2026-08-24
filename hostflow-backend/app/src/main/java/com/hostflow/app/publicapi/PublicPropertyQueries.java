package com.hostflow.app.publicapi;

import com.hostflow.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * NazilCo is a marketplace across ALL HostFlow-managed properties, so public
 * browsing is deliberately cross-tenant by design (decision made during the
 * frontend/backend reconciliation pass, item 6). Uses platformAdminJdbcTemplate
 * (BYPASSRLS) since there is no tenant context for an anonymous request to
 * filter
 * by — this is the correct, intended exception, not a workaround.
 *
 * SECURITY NOTE: unlike PlatformBillingQueries/PlatformBookingQueries (which
 * only
 * ever serve PLATFORM_ADMIN-gated screens), this class's queries are reachable
 * by
 * ANONYMOUS internet traffic. All SQL here is parameterized (never string-
 * concatenated), keeping the BYPASSRLS exposure contained to exactly these
 * fixed
 * query shapes. Recommended future hardening: a dedicated low-privilege
 * "hostflow_public_read" Postgres role (SELECT-only, no BYPASSRLS needed if a
 * simpler cross-tenant view is used instead) rather than reusing the
 * platform-admin
 * pool for anonymous traffic — flagged as a follow-up, not blocking this build.
 */
@Component
public class PublicPropertyQueries {

    private final JdbcTemplate jdbcTemplate;

    public PublicPropertyQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record PublicPropertyRow(UUID id, String name, String description, String propertyType,
            String rentalModel, String addressLine, String city, String country,
            Double latitude, Double longitude, BigDecimal basePrice) {
    }

    private static final RowMapper<PublicPropertyRow> ROW_MAPPER = (rs, rowNum) -> new PublicPropertyRow(
            UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getString("description"),
            rs.getString("property_type"), rs.getString("rental_model"), rs.getString("address_line"),
            rs.getString("city"), rs.getString("country"),
            rs.getObject("latitude") != null ? rs.getDouble("latitude") : null,
            rs.getObject("longitude") != null ? rs.getDouble("longitude") : null,
            rs.getBigDecimal("base_price"));

    public List<PublicPropertyRow> list(int limit, int offset) {
        String sql = "SELECT id, name, description, property_type, rental_model, address_line, city, country, " +
                "latitude, longitude, base_price FROM properties WHERE status = 'ACTIVE' " +
                "ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, limit, offset);
    }

    public List<PublicPropertyRow> search(String city, String propertyType, String rentalModel,
            BigDecimal minPrice, BigDecimal maxPrice,
            int limit, int offset) {
        String sql = """
                SELECT id, name, description, property_type, rental_model, address_line, city, country,
                       latitude, longitude, base_price
                FROM properties
                WHERE status = 'ACTIVE'
                  AND (?::text IS NULL OR city ILIKE '%' || ? || '%')
                  AND (?::text IS NULL OR property_type = ?)
                  AND (?::text IS NULL OR rental_model = ?)
                  AND (?::numeric IS NULL OR base_price >= ?)
                  AND (?::numeric IS NULL OR base_price <= ?)
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER,
                city, city, propertyType, propertyType, rentalModel, rentalModel,
                minPrice, minPrice, maxPrice, maxPrice, limit, offset);
    }

    /**
     * Deliberately does NOT distinguish "not found" from "exists but not ACTIVE" in
     * the error response — both return the same 404, so a caller cannot probe for
     * the existence of DRAFT/ARCHIVED properties belonging to other tenants.
     */
    public PublicPropertyRow findPublicById(UUID propertyId) {
        String sql = "SELECT id, name, description, property_type, rental_model, address_line, city, country, " +
                "latitude, longitude, base_price FROM properties WHERE id = ? AND status = 'ACTIVE'";
        List<PublicPropertyRow> results = jdbcTemplate.query(sql, ROW_MAPPER, propertyId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Property", propertyId);
        }
        return results.get(0);
    }
}
