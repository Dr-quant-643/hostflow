package com.hostflow.tenancy.context;

import com.hostflow.common.exception.TenantContextMissingException;

import java.util.UUID;

/**
 * ThreadLocal holder for the current request's tenant id.
 *
 * Set by a tenant-resolution filter (TenantHeaderFilter for now — dev/test
 * only;
 * replaced by a Keycloak JWT claim filter in core-security, module 5) at the
 * start
 * of each request, and read by TenantAwareJpaTransactionManager to issue SET
 * LOCAL
 * on every transaction. ALWAYS cleared in a finally block by whatever sets it,
 * to
 * avoid leaking tenant context across pooled threads.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID get() {
        return CURRENT_TENANT.get();
    }

    /**
     * Use this in code paths that require a tenant to be present (i.e. almost
     * everywhere
     * except platform-admin background jobs). Fails loudly and immediately rather
     * than
     * silently proceeding with no tenant scoping.
     */
    public static UUID require() {
        UUID tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new TenantContextMissingException();
        }
        return tenantId;
    }

    public static boolean isSet() {
        return CURRENT_TENANT.get() != null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
