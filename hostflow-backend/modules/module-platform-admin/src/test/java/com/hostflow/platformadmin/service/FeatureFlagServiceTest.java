package com.hostflow.platformadmin.service;

import com.hostflow.platformadmin.entity.FeatureFlag;
import com.hostflow.platformadmin.repository.FeatureFlagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository repository;

    private FeatureFlagService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new FeatureFlagService(repository);
    }

    @Test
    void isEnabled_returnsFalse_whenNoFlagExists() {
        when(repository.findByKeyAndScopeOrgIdIsNull("new_feature")).thenReturn(Optional.empty());

        assertThat(service.isEnabled("new_feature", null)).isFalse();
    }

    @Test
    void isEnabled_orgOverride_takesPriorityOverGlobal() {
        UUID orgId = UUID.randomUUID();
        FeatureFlag globalFlag = new FeatureFlag("beta_feature", null, false, null);
        FeatureFlag orgOverride = new FeatureFlag("beta_feature", orgId, true, null);
        when(repository.findByKeyAndScopeOrgId("beta_feature", orgId)).thenReturn(Optional.of(orgOverride));

        assertThat(service.isEnabled("beta_feature", orgId)).isTrue();
    }

    @Test
    void isEnabled_fallsBackToGlobal_whenNoOrgOverride() {
        UUID orgId = UUID.randomUUID();
        when(repository.findByKeyAndScopeOrgId("beta_feature", orgId)).thenReturn(Optional.empty());
        when(repository.findByKeyAndScopeOrgIdIsNull("beta_feature"))
                .thenReturn(Optional.of(new FeatureFlag("beta_feature", null, true, null)));

        assertThat(service.isEnabled("beta_feature", orgId)).isTrue();
    }
}
