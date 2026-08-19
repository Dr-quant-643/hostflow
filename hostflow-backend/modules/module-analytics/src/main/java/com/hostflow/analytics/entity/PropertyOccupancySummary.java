package com.hostflow.analytics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Maps to the mv_property_occupancy_summary materialized view (V12 migration).
 * @Immutable tells Hibernate this entity is read-only and should never attempt
 * dirty-checking or UPDATE statements against it — appropriate since materialized
 * views are refreshed via REFRESH MATERIALIZED VIEW, never via application writes.
 * NOT a TenantScopedEntity — tenant_id here is a plain column carried through from
 * the underlying tables, filtered explicitly in repository queries (see
 * AnalyticsRepository) rather than via RLS, since materialized views do not support
 * RLS policies in Postgres. This is a deliberate, documented exception to the
 * RLS-everywhere pattern used throughout the rest of the codebase.
 */
@Entity
@Immutable
@Table(name = "mv_property_occupancy_summary")
public class PropertyOccupancySummary {

    @Id
    @Column(name = "property_id")
    private UUID propertyId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "property_name")
    private String propertyName;

    @Column(name = "total_bookings")
    private Long totalBookings;

    @Column(name = "total_nights_booked")
    private Long totalNightsBooked;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    protected PropertyOccupancySummary() {
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public Long getTotalNightsBooked() {
        return totalNightsBooked;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}
