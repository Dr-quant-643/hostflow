package com.hostflow.booking.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One synced VEVENT (blocked date range) from an ExternalCalendarLink's
 * .ics feed. externalUid is the VEVENT's UID line, used to upsert on each
 * sync (an OTA re-exports the same UID for a still-blocked range, a new UID
 * for a newly-blocked one) rather than deleting and recreating every block
 * on every poll.
 */
@Entity
@Table(name = "external_calendar_blocks")
public class ExternalCalendarBlock extends TenantScopedEntity {

    @Column(name = "link_id", nullable = false)
    private UUID linkId;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "external_uid", nullable = false)
    private String externalUid;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    protected ExternalCalendarBlock() {
    }

    public ExternalCalendarBlock(UUID linkId, UUID propertyId, String externalUid, LocalDate startDate, LocalDate endDate) {
        this.linkId = linkId;
        this.propertyId = propertyId;
        this.externalUid = externalUid;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getLinkId() {
        return linkId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getExternalUid() {
        return externalUid;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
