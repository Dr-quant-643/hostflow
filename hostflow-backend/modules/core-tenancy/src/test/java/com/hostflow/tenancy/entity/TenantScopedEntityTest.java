package com.hostflow.tenancy.entity;

import com.hostflow.common.exception.TenantContextMissingException;
import com.hostflow.tenancy.config.TestTenancyConfig;
import com.hostflow.tenancy.context.TenantContext;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * H2 only verifies the application-level defense-in-depth layer
 * (auto-assignment of
 * tenant_id via @PrePersist). It CANNOT verify Postgres RLS itself — H2 doesn't
 * support
 * RLS/SET LOCAL semantics the same way. Real end-to-end RLS verification (SET
 * LOCAL ->
 * policy -> row visibility) is deferred to a Testcontainers Postgres
 * integration test
 * in the app module (module 15), which will use the tenancy_smoke_test table
 * from
 * V4__tenancy_smoke_test_table.sql.
 */
@DataJpaTest
@Import(TestTenancyConfig.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestTenancyConfig.class)
class TenantScopedEntityTest {

    @Entity
    @Table(name = "test_tenant_entity")
    static class TestTenantEntity extends TenantScopedEntity {
        private String label;

        protected TestTenantEntity() {
        }

        TestTenantEntity(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        TenantContext.clear();
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void persisting_autoAssignsTenantId_fromTenantContext() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        TestTenantEntity entity = new TestTenantEntity("sample");
        TestTenantEntity persisted = entityManager.persistFlushFind(entity);

        assertThat(persisted.getTenantId()).isEqualTo(tenantId);
        assertThat(persisted.getLabel()).isEqualTo("sample");
    }

    @Test
    void persisting_withoutTenantContext_throwsTenantContextMissingException() {
        TestTenantEntity entity = new TestTenantEntity("sample");

        // Use isInstanceOf directly instead of hasRootCauseInstanceOf
        assertThatThrownBy(() -> entityManager.persistFlushFind(entity))
                .isInstanceOf(TenantContextMissingException.class)
                .hasMessageContaining("No tenant context found");
    }

    @Test
    void persistingWithExistingTenantId_preservesIt() {
        UUID tenantId = UUID.randomUUID();
        UUID differentTenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        TestTenantEntity entity = new TestTenantEntity("sample");
        entity.setTenantId(differentTenantId);

        TestTenantEntity persisted = entityManager.persistFlushFind(entity);

        assertThat(persisted.getTenantId()).isEqualTo(differentTenantId);
        assertThat(persisted.getTenantId()).isNotEqualTo(tenantId);
    }

    @Test
    void updatingEntity_doesNotChangeTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        TestTenantEntity entity = new TestTenantEntity("original");
        TestTenantEntity persisted = entityManager.persistFlushFind(entity);
        UUID originalTenantId = persisted.getTenantId();

        persisted.setLabel("updated");
        entityManager.persistAndFlush(persisted);
        entityManager.clear();

        TestTenantEntity reloaded = entityManager.find(TestTenantEntity.class, persisted.getId());
        assertThat(reloaded.getTenantId()).isEqualTo(originalTenantId);
        assertThat(reloaded.getLabel()).isEqualTo("updated");
    }
}
