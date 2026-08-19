package com.hostflow.office.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "office_meeting_rooms")
public class MeetingRoom extends TenantScopedEntity {

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected MeetingRoom() {
    }

    public MeetingRoom(UUID propertyId, String name, Integer capacity) {
        this.propertyId = propertyId;
        this.name = name;
        this.capacity = capacity;
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public boolean isActive() {
        return active;
    }
}
