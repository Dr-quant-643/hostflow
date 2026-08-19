-- Helper function so RLS policies (added per-table by each future module) can all
-- reference the same tenant-resolution logic instead of repeating
-- current_setting('app.current_tenant') inline in every policy.
--
-- Returns NULL if no tenant context was set for the session/transaction, which
-- causes any RLS policy using this function to deny all rows by default —
-- fail-closed, not fail-open.

CREATE OR REPLACE FUNCTION current_tenant_id() RETURNS UUID AS $$
    SELECT NULLIF(current_setting('app.current_tenant', true), '')::UUID;
$$ LANGUAGE SQL STABLE;

COMMENT ON FUNCTION current_tenant_id() IS
'Resolves the tenant_id for the current transaction from the app.current_tenant session variable, set per-request by core-tenancy''s interceptor via SET LOCAL. Returns NULL (fail-closed) if unset.';
