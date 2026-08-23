package com.hostflow.tenancy.config;

import com.hostflow.tenancy.context.TenantContext;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

/**
 * Extends Spring's JpaTransactionManager to run:
 *     SET LOCAL app.current_tenant = '<tenant-id>'
 * at the start of every transaction, using SET LOCAL (transaction-scoped) rather than
 * plain SET (session-scoped) specifically because HikariCP reuses pooled connections
 * across requests — a plain SET would leak tenant context into whichever request
 * borrows that connection next. SET LOCAL is automatically reset when the transaction
 * ends, regardless of commit or rollback.
 *
 * Requires setDataSource() to also be called on this bean (done in
 * TenantTransactionManagerConfig) — that's what makes the underlying JDBC Connection
 * resolvable via TransactionSynchronizationManager here.
 *
 * If no tenant is set in TenantContext (e.g. an unauthenticated public endpoint, or a
 * platform-admin background job using the hostflow_platform_admin BYPASSRLS role),
 * this deliberately does nothing — current_tenant_id() then returns NULL, and RLS
 * policies fail-closed (deny all rows) for any role that isn't BYPASSRLS.
 */
public class TenantAwareJpaTransactionManager extends JpaTransactionManager {

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);

        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            return;
        }

        Connection connection = resolveCurrentConnection();
        if (connection == null) {
            // A tenant IS set, so this transaction must be scoped — silently
            // proceeding unscoped would mean current_tenant_id() returns NULL and
            // RLS policies evaluate `tenant_id = NULL`, which Postgres treats as
            // unknown/false and fails closed (denies all rows) for a correctly
            // written policy. That's the safe failure mode, but relying on every
            // future RLS policy being written that carefully is not a bet worth
            // making — fail loudly here instead so a connection-resolution bug
            // surfaces immediately instead of silently degrading isolation.
            throw new IllegalStateException(
                    "Tenant context is set (" + tenantId + ") but no JDBC connection was resolvable to scope it to");
        }

        try (Statement statement = connection.createStatement()) {
            // Tenant id is a UUID from our own TenantContext (never raw user input concatenated
            // directly from a request), so string interpolation here is not a SQL injection vector.
            statement.execute("SET LOCAL app.current_tenant = '" + tenantId + "'");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set tenant context on transaction", e);
        }
    }

    private Connection resolveCurrentConnection() {
        Object resource = TransactionSynchronizationManager.getResource(getDataSource());
        if (resource instanceof ConnectionHolder connectionHolder) {
            return connectionHolder.getConnection();
        }
        return null;
    }
}
