package com.hostflow.maintenance.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * "Preventive maintenance" from the vision doc — a recurring schedule (e.g.
 * "service the HVAC every 3 months") that a scheduled job (see
 * PreventiveMaintenanceJob) reads to auto-generate WorkOrders when due, rather
 * than only ever reacting to reported problems.
 */
@Entity
@Table(name = "maintenance_schedules")
public class MaintenanceSchedule extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "asset_id")
    private UUID assetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MaintenanceCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected MaintenanceSchedule() {
    }

    public MaintenanceSchedule(UUID propertyId, UUID assetId, MaintenanceCategory category, String title,
                                Integer intervalDays, LocalDate nextDueDate) {
        this.propertyId = propertyId;
        this.assetId = assetId;
        this.category = category;
        this.title = title;
        this.intervalDays = intervalDays;
        this.nextDueDate = nextDueDate;
    }

    public void rollToNextDueDate() {
        this.nextDueDate = this.nextDueDate.plusDays(intervalDays);
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public UUID getAssetId() {
        return assetId;
    }

    public MaintenanceCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public boolean isActive() {
        return active;
    }
}
