package com.hostflow.app.publicapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * A self-contained, no-external-service demand-based pricing heuristic --
 * deliberately NOT calling out to an LLM or third-party revenue-management
 * API (the user explicitly wants this feature to cost nothing extra to
 * run). Forward occupancy (nights already booked in the next 30 days) is
 * the primary signal, not trailing occupancy: in revenue management terms,
 * how fast a property is currently filling up is the stronger indicator of
 * whether to raise or lower price than how full it WAS last month.
 *
 * Scoped to NIGHTLY properties only for v1 -- MONTHLY leases don't have a
 * comparable "nights booked in the next 30 days" signal (a lease is booked
 * in months, not nights), and would need a different heuristic entirely.
 *
 * Deliberately a SUGGESTION, not an auto-applied price change: the owner
 * reviews and applies it via the existing PATCH /properties/{id} basePrice
 * update, same as any manual price edit -- no new mutation endpoint needed.
 */
@Component
public class PricingSuggestionQueries {

    private final JdbcTemplate platformAdminJdbcTemplate;

    public PricingSuggestionQueries(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
    }

    public record PricingSuggestionRow(UUID propertyId, String propertyName, BigDecimal currentPrice,
            BigDecimal suggestedPrice, int changePercent, int forwardOccupancyPercent, int trailingOccupancyPercent,
            String reason) {
    }

    public List<PricingSuggestionRow> suggestionsForOwner(UUID ownerUserId) {
        String sql = """
                SELECT p.id, p.name, p.base_price,
                    COALESCE(SUM(CASE
                        WHEN b.check_in >= CURRENT_DATE - INTERVAL '30 days' AND b.check_in < CURRENT_DATE
                             AND b.status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')
                        THEN (b.check_out - b.check_in) ELSE 0 END), 0) AS trailing_nights,
                    COALESCE(SUM(CASE
                        WHEN b.check_in >= CURRENT_DATE AND b.check_in < CURRENT_DATE + INTERVAL '30 days'
                             AND b.status IN ('PENDING','CONFIRMED','CHECKED_IN')
                        THEN (b.check_out - b.check_in) ELSE 0 END), 0) AS forward_nights
                FROM properties p
                LEFT JOIN bookings b ON b.property_id = p.id
                WHERE p.owner_user_id = ? AND p.rental_model = 'NIGHTLY' AND p.status = 'ACTIVE' AND p.base_price IS NOT NULL
                GROUP BY p.id, p.name, p.base_price
                ORDER BY p.name
                """;
        return platformAdminJdbcTemplate.query(sql, (rs, rowNum) -> {
            UUID propertyId = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            BigDecimal basePrice = rs.getBigDecimal("base_price");
            int trailingNights = rs.getInt("trailing_nights");
            int forwardNights = rs.getInt("forward_nights");

            int forwardPct = Math.min(100, Math.round(forwardNights * 100f / 30f));
            int trailingPct = Math.min(100, Math.round(trailingNights * 100f / 30f));

            return buildSuggestion(propertyId, name, basePrice, forwardPct, trailingPct);
        }, ownerUserId);
    }

    private PricingSuggestionRow buildSuggestion(UUID propertyId, String name, BigDecimal basePrice,
            int forwardPct, int trailingPct) {
        int changePercent;
        String reason;
        if (forwardPct >= 70) {
            changePercent = 15;
            reason = "Strong demand: " + forwardPct + "% of the next 30 nights are already booked.";
        } else if (forwardPct >= 40) {
            changePercent = 0;
            reason = "Healthy booking pace (" + forwardPct + "% of the next 30 nights booked) -- no change suggested.";
        } else if (forwardPct >= 15) {
            changePercent = -10;
            reason = "Below-average pace: only " + forwardPct + "% of the next 30 nights are booked so far.";
        } else {
            changePercent = -20;
            reason = "Weak demand: just " + forwardPct + "% of the next 30 nights are booked.";
        }

        BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(changePercent).divide(BigDecimal.valueOf(100)));
        BigDecimal suggestedPrice = basePrice.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);

        return new PricingSuggestionRow(propertyId, name, basePrice, suggestedPrice, changePercent, forwardPct,
                trailingPct, reason);
    }
}
