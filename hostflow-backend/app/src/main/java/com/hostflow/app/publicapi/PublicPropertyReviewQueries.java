package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Reviews are guest-write-only elsewhere in the app (module-review has no
 * guest-facing GET) — this is the one place a review is ever read back, for
 * anonymous display on a property's public page. Same platformAdminJdbcTemplate
 * (BYPASSRLS) + status='ACTIVE' fail-safe as the rest of PublicPropertyController.
 * Left-joins guest_profiles for a display name since reviews only store the
 * reviewer's Keycloak subject id; a guest who deleted their profile still shows
 * as "Guest" rather than dropping the review.
 */
@Component
public class PublicPropertyReviewQueries {

    private final JdbcTemplate jdbcTemplate;

    public PublicPropertyReviewQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public record PublicReviewRow(UUID id, Integer rating, String comment, String guestName, Instant createdAt) {
    }

    public List<PublicReviewRow> listForProperty(UUID propertyId) {
        String sql = """
                SELECT r.id, r.rating, r.comment, r.created_at, gp.first_name
                FROM reviews r
                JOIN properties p ON p.id = r.property_id
                LEFT JOIN guest_profiles gp ON gp.keycloak_id = r.reviewer_user_id::text
                WHERE r.property_id = ? AND p.status = 'ACTIVE'
                ORDER BY r.created_at DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new PublicReviewRow(
                UUID.fromString(rs.getString("id")),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getString("first_name") != null ? rs.getString("first_name") : "Guest",
                rs.getTimestamp("created_at").toInstant()
        ), propertyId);
    }
}
