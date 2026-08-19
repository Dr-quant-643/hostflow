package com.hostflow.app.integration;

import com.hostflow.app.AbstractIntegrationTest;
import com.hostflow.tenancy.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * THE test that has been deferred and referenced since core-tenancy's V4 migration:
 * proves the full chain — TenantContext.set() -> TenantAwareJpaTransactionManager's
 * SET LOCAL -> Postgres RLS policy -> row visibility — actually works end-to-end
 * against REAL Postgres (H2 cannot verify this, as documented repeatedly throughout
 * this build). Uses core-tenancy's own tenancy_smoke_test table (V4 migration),
 * the exact reference table built for this purpose.
 */
class TenantIsolationIT extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void rowsInsertedForOneTenant_areInvisibleWhenQueryingAsAnotherTenant() {
        UUID tenantA = UUID.randomUUID();
        UUID tenantB = UUID.randomUUID();
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        // Insert a row as tenant A.
        TenantContext.set(tenantA);
        txTemplate.executeWithoutResult(status ->
                jdbcTemplate.update(
                        "INSERT INTO tenancy_smoke_test (tenant_id, label) VALUES (?, ?)",
                        tenantA, "belongs-to-tenant-a"));
        TenantContext.clear();

        // Query as tenant B — the row must be invisible, proving RLS (not just
        // application-level filtering) is doing the enforcement.
        TenantContext.set(tenantB);
        List<String> visibleToTenantB = txTemplate.execute(status ->
                jdbcTemplate.queryForList("SELECT label FROM tenancy_smoke_test", String.class));
        TenantContext.clear();

        assertThat(visibleToTenantB).isEmpty();

        // Query again as tenant A — the row IS visible to its own tenant.
        TenantContext.set(tenantA);
        List<String> visibleToTenantA = txTemplate.execute(status ->
                jdbcTemplate.queryForList("SELECT label FROM tenancy_smoke_test", String.class));

        assertThat(visibleToTenantA).containsExactly("belongs-to-tenant-a");
    }

    @Test
    void queryingWithNoTenantContextSet_seesNoRows_failClosed() {
        // No TenantContext.set() call at all — current_tenant_id() resolves to NULL,
        // and per core-persistence's V3 migration, this must fail closed (zero rows),
        // never fail open (all rows).
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        List<String> visible = txTemplate.execute(status ->
                jdbcTemplate.queryForList("SELECT label FROM tenancy_smoke_test", String.class));

        assertThat(visible).isEmpty();
    }
}
