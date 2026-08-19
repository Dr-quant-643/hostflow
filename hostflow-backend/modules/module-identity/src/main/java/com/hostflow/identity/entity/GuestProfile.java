package com.hostflow.identity.entity;

import com.hostflow.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Deliberately extends BaseEntity, NOT TenantScopedEntity — same reasoning as
 * Organization: a guest does not belong to any single tenant, since NazilCo is
 * a
 * cross-tenant marketplace (per the reconciliation decision on public property
 * browsing). This is the guest-side counterpart to Organization being the
 * non-tenant-scoped root on the XanuOS side.
 */
@Entity
@Table(name = "guest_profiles")
public class GuestProfile extends BaseEntity {

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected GuestProfile() {
    }

    public GuestProfile(String keycloakId, String email, String firstName, String lastName, String phone) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
