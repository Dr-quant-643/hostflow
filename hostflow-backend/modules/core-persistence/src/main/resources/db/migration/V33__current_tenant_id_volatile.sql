-- current_tenant_id() was declared STABLE, which told Postgres's planner it
-- is safe to evaluate once and reuse the result within a cached/generic plan.
-- PgJDBC promotes a repeatedly-executed prepared statement (e.g. Hibernate's
-- parameterized findAll queries, reused across MANY different tenants' requests
-- on the same long-lived HikariCP-pooled connection) to a server-side generic
-- plan after prepareThreshold executions (default 5) — and once that happens,
-- this STABLE function's result can get baked into that cached plan from
-- whichever transaction first triggered the promotion, silently ignoring the
-- SET LOCAL app.current_tenant value of every later transaction that reuses
-- the same physical connection. This is how a real, reproducible cross-tenant
-- RLS bypass happened despite SET LOCAL and the RLS policy both being
-- individually correct. VOLATILE forces Postgres to re-evaluate this function
-- on every single invocation, never caching it across executions or plans.
CREATE OR REPLACE FUNCTION current_tenant_id() RETURNS UUID AS $$
    SELECT NULLIF(current_setting('app.current_tenant', true), '')::UUID;
$$ LANGUAGE SQL VOLATILE;
