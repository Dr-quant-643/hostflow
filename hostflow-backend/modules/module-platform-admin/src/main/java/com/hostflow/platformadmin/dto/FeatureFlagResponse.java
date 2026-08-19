package com.hostflow.platformadmin.dto;

import com.hostflow.platformadmin.entity.FeatureFlag;

import java.util.UUID;

public record FeatureFlagResponse(UUID id, String key, UUID scopeOrgId, boolean enabled, String description) {
    public static FeatureFlagResponse from(FeatureFlag f) {
        return new FeatureFlagResponse(f.getId(), f.getKey(), f.getScopeOrgId(), f.isEnabled(), f.getDescription());
    }
}
