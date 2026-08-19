package com.hostflow.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.hostflow.persistence.config.JpaAuditingConfig;
import com.hostflow.persistence.config.TestJpaConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Uses H2 in-memory DB (auto-configured by @DataJpaTest) specifically so this
 * module's tests have zero external dependencies (no Docker, no real Postgres
 * needed to run `mvn test`). Postgres-specific behavior (RLS, pg_trgm, the
 * role migrations) is verified separately once core-tenancy and the app module
 * bring in Testcontainers for full-stack integration tests against real
 * Postgres.
 */
@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class BaseEntityTest {

    @Entity
    @Table(name = "test_entity")
    static class TestEntity extends BaseEntity {
        private String name;

        protected TestEntity() {
        }

        TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void persistingEntity_generatesUuidAndAuditTimestamps() {
        TestEntity entity = new TestEntity("sample");

        TestEntity persisted = entityManager.persistFlushFind(entity);

        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
        assertThat(persisted.getVersion()).isEqualTo(0L);
    }

    @Test
    void updatingEntity_bumpsVersionAndUpdatedAt_butNotCreatedAt() {
        TestEntity entity = entityManager.persistFlushFind(new TestEntity("original"));
        var originalCreatedAt = entity.getCreatedAt();
        var originalVersion = entity.getVersion();

        entity.setName("changed");
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        TestEntity reloaded = entityManager.find(TestEntity.class, entity.getId());

        assertThat(reloaded.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(reloaded.getVersion()).isGreaterThan(originalVersion);
    }

    @Test
    void twoEntitiesWithSameId_areEqual_regardlessOfOtherFields() {
        TestEntity a = entityManager.persistFlushFind(new TestEntity("a"));
        entityManager.clear();
        TestEntity reloaded = entityManager.find(TestEntity.class, a.getId());

        assertThat(reloaded).isEqualTo(a);
    }

    @Test
    void unsavedEntities_areNeverEqual() {
        TestEntity a = new TestEntity("a");
        TestEntity b = new TestEntity("a");

        assertThat(a).isNotEqualTo(b);
    }
}
