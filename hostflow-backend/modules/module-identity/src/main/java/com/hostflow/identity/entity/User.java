package com.hostflow.identity.entity;

import com.hostflow.tenancy.entity.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends TenantScopedEntity {

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected User() {
    }

    public User(String keycloakId, String email, String firstName, String lastName, Set<UserRole> roles) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    /**
     * NEW: replaces the full role set. Used by OrgUserAdminService's PATCH roles
     * endpoint. Deliberately a full replace, not add/remove — matches the
     * frontend's
     * expected "set roles to exactly this list" semantics, and keeps the Postgres
     * side and the Keycloak-sync side (UserRoleSyncService) diffing against the
     * same before/after shape.
     */
    public void replaceRoles(Set<UserRole> newRoles) {
        this.roles = new HashSet<>(newRoles);
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

    public Set<UserRole> getRoles() {
        return roles;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
