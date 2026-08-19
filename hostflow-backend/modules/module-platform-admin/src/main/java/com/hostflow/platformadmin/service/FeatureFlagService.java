package com.hostflow.platformadmin.service;

import com.hostflow.platformadmin.entity.FeatureFlag;
import com.hostflow.platformadmin.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository repository;

    public FeatureFlagService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    /**
     * Resolution order: org-specific override (if one exists) wins; otherwise
     * falls back to the global default; otherwise defaults to false (unknown flags
     * are off, not on — fail-closed, same philosophy as RLS's fail-closed design).
     */
    @Transactional(readOnly = true)
    public boolean isEnabled(String key, UUID orgId) {
        if (orgId != null) {
            var override = repository.findByKeyAndScopeOrgId(key, orgId);
            if (override.isPresent()) {
                return override.get().isEnabled();
            }
        }
        return repository.findByKeyAndScopeOrgIdIsNull(key).map(FeatureFlag::isEnabled).orElse(false);
    }

    @Transactional
    public FeatureFlag setGlobalFlag(String key, boolean enabled, String description) {
        return repository.findByKeyAndScopeOrgIdIsNull(key)
                .map(existing -> {
                    existing.toggle(enabled);
                    return existing;
                })
                .orElseGet(() -> repository.save(new FeatureFlag(key, null, enabled, description)));
    }

    @Transactional
    public FeatureFlag setOrgOverride(String key, UUID orgId, boolean enabled) {
        return repository.findByKeyAndScopeOrgId(key, orgId)
                .map(existing -> {
                    existing.toggle(enabled);
                    return existing;
                })
                .orElseGet(() -> repository.save(new FeatureFlag(key, orgId, enabled, null)));
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> listAll() {
        return repository.findAll();
    }
}
