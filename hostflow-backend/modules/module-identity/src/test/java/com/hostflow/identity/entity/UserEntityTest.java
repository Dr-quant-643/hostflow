package com.hostflow.identity.entity;

import com.hostflow.identity.config.TestJpaConfig;
import com.hostflow.persistence.config.JpaAuditingConfig;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
@Import(JpaAuditingConfig.class) // Add this to enable auditing
@ActiveProfiles("test")
class UserEntityTest {

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
    void contextLoads() {
        assertThat(entityManager).isNotNull();
    }

    @Test
    void persistingUser_autoAssignsTenantId_andPersistsRoles() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        User user = new User("kc-123", "test@hostflow.com", "Test", "User", Set.of(UserRole.XANUOS_MANAGER));
        User persisted = entityManager.persistFlushFind(user);

        assertThat(persisted.getTenantId()).isEqualTo(tenantId);
        assertThat(persisted.getRoles()).containsExactly(UserRole.XANUOS_MANAGER);
        assertThat(persisted.getEmail()).isEqualTo("test@hostflow.com");
        assertThat(persisted.isActive()).isTrue();
        assertThat(persisted.getCreatedAt()).isNotNull(); // Now should pass
        assertThat(persisted.getUpdatedAt()).isNotNull(); // Now should pass
    }

    @Test
    void persistingUser_withoutTenantContext_throwsException() {
        User user = new User("kc-456", "test2@hostflow.com", "Test2", "User2", Set.of(UserRole.XANUOS_STAFF));

        // Use isInstanceOf instead of hasRootCauseInstanceOf
        assertThatThrownBy(() -> entityManager.persistFlushFind(user))
                .isInstanceOf(com.hostflow.common.exception.TenantContextMissingException.class);
    }

    @Test
    void updatingUser_preservesTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        User user = new User("kc-789", "update@hostflow.com", "Update", "User", Set.of(UserRole.XANUOS_OWNER));
        User persisted = entityManager.persistFlushFind(user);
        UUID originalTenantId = persisted.getTenantId();

        persisted.deactivate();
        entityManager.persistAndFlush(persisted);
        entityManager.clear();

        User reloaded = entityManager.find(User.class, persisted.getId());
        assertThat(reloaded.getTenantId()).isEqualTo(originalTenantId);
        assertThat(reloaded.isActive()).isFalse();
        assertThat(reloaded.getUpdatedAt()).isAfter(reloaded.getCreatedAt());
    }

    @Test
    void userEntity_hasCorrectFields() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        User user = new User("kc-test-123", "fields@hostflow.com", "Field", "Test", Set.of(UserRole.PLATFORM_ADMIN));
        User persisted = entityManager.persistFlushFind(user);

        assertThat(persisted.getKeycloakId()).isEqualTo("kc-test-123");
        assertThat(persisted.getEmail()).isEqualTo("fields@hostflow.com");
        assertThat(persisted.getFirstName()).isEqualTo("Field");
        assertThat(persisted.getLastName()).isEqualTo("Test");
        assertThat(persisted.getRoles()).contains(UserRole.PLATFORM_ADMIN);
        assertThat(persisted.isActive()).isTrue();
        assertThat(persisted.getTenantId()).isEqualTo(tenantId);
        assertThat(persisted.getCreatedAt()).isNotNull();
    }

    @Test
    void userEntity_usesBaseEntityAuditing() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        User user = new User("kc-audit", "audit@hostflow.com", "Audit", "Test", Set.of(UserRole.XANUOS_STAFF));
        User persisted = entityManager.persistFlushFind(user);

        assertThat(persisted.getCreatedAt()).isNotNull();
        assertThat(persisted.getUpdatedAt()).isNotNull();
        assertThat(persisted.getVersion()).isEqualTo(0L);
    }
}
