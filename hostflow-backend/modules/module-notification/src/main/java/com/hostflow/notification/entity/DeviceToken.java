package com.hostflow.notification.entity;

import com.hostflow.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

/**
 * Deliberately extends BaseEntity, NOT TenantScopedEntity — same reasoning as
 * GuestProfile. userId is a raw Keycloak subject id, not a User/GuestProfile
 * foreign key, matching the convention used everywhere else a guest-facing
 * table references "the current user" (bookings.guest_user_id,
 * rental_tenants.linked_user_id): staff Users are tenant-scoped, guests are
 * not, so a single tenant-less table keyed on the Keycloak sub is what lets
 * PushDeliveryService resolve a real "to" address for either kind of account
 * without module-notification needing a dependency on module-identity.
 */
@Entity
@Table(name = "device_tokens", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_token"}))
public class DeviceToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(name = "platform")
    private String platform;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected DeviceToken() {
    }

    public DeviceToken(UUID userId, String deviceToken, String platform) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.platform = platform;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }
}
