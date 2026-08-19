package com.hostflow.platformadmin.repository;

import com.hostflow.platformadmin.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, UUID> {
    List<FeatureFlag> findByKey(String key);
    Optional<FeatureFlag> findByKeyAndScopeOrgIdIsNull(String key);
    Optional<FeatureFlag> findByKeyAndScopeOrgId(String key, UUID scopeOrgId);
}
