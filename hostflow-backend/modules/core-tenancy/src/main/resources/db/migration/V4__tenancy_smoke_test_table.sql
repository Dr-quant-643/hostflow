-- Reference pattern to COPY for every tenant-owned table in every future module
-- (module-property, module-booking, module-crm, etc.):
--
--   1. tenant_id UUID NOT NULL
--   2. ALTER TABLE <table> ENABLE ROW LEVEL SECURITY
--   3. ALTER TABLE <table> FORCE ROW LEVEL SECURITY   -- applies RLS even to the table owner role
--   4. CREATE POLICY tenant_isolation ON <table>
--        USING (tenant_id = current_tenant_id())
--        WITH CHECK (tenant_id = current_tenant_id())
--   5. GRANT appropriate privileges to hostflow_app (never BYPASSRLS)
--
-- This migration creates one concrete table so the full chain — TenantContext ->
-- SET LOCAL -> RLS policy -> row visibility — can be verified end-to-end by a real
-- Postgres Testcontainers integration test in the app module (module 15).

CREATE TABLE IF NOT EXISTS tenancy_smoke_test (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    label TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE tenancy_smoke_test ENABLE ROW LEVEL SECURITY;
ALTER TABLE tenancy_smoke_test FORCE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation ON tenancy_smoke_test
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON tenancy_smoke_test TO hostflow_app;
