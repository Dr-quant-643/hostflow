package com.hostflow.booking.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * An owner-pasted iCal (.ics) feed URL from an external booking channel
 * (Airbnb, Booking.com, VRBO all publish free per-listing .ics export URLs,
 * no partner API approval needed) -- ExternalCalendarSyncJob polls this URL
 * periodically and turns its VEVENTs into ExternalCalendarBlock rows, which
 * BookingAvailabilityService/PublicAvailabilityQueries treat the same as a
 * real Booking for the purpose of blocking dates. This is what prevents
 * double-booking across platforms without needing a paid channel-manager
 * subscription or OTA partner API access -- see PricingSuggestionQueries'
 * sibling doc comment on module scope for the same "no new cost" reasoning.
 */
@Entity
@Table(name = "external_calendar_links")
public class ExternalCalendarLink extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "ics_url", nullable = false)
    private String icsUrl;

    @Column(name = "label")
    private String label;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "last_sync_error")
    private String lastSyncError;

    protected ExternalCalendarLink() {
    }

    public ExternalCalendarLink(UUID propertyId, String icsUrl, String label) {
        this.propertyId = propertyId;
        this.icsUrl = icsUrl;
        this.label = label;
    }

    public void markSynced() {
        this.lastSyncedAt = Instant.now();
        this.lastSyncError = null;
    }

    public void markSyncFailed(String error) {
        this.lastSyncedAt = Instant.now();
        this.lastSyncError = error;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getIcsUrl() {
        return icsUrl;
    }

    public String getLabel() {
        return label;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public String getLastSyncError() {
        return lastSyncError;
    }
}
