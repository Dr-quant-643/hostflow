package com.hostflow.platformadmin.entity;

import com.hostflow.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Not tenant-scoped — a feature flag is a platform-wide toggle (e.g. "is the new
 * marketing module enabled"), same category of entity as Organization/GuestProfile.
 * scopeOrgId, when non-null, allows PER-ORGANIZATION overrides (a flag enabled
 * platform-wide but explicitly disabled for one org, or vice versa) without
 * needing a separate override table — null scopeOrgId means "the global default."
 */
@Entity
@Table(name = "feature_flags")
public class FeatureFlag extends BaseEntity {

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "scope_org_id")
    private java.util.UUID scopeOrgId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "description")
    private String description;

    protected FeatureFlag() {
    }

    public FeatureFlag(String key, java.util.UUID scopeOrgId, boolean enabled, String description) {
        this.key = key;
        this.scopeOrgId = scopeOrgId;
        this.enabled = enabled;
        this.description = description;
    }

    public void toggle(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public java.util.UUID getScopeOrgId() {
        return scopeOrgId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }
}
