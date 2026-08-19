-- hostflow_platform_admin is BYPASSRLS, but BYPASSRLS only skips row-level
-- security policies -- it does NOT imply table-level GRANT privileges. Of the
-- 25 modules in this backend, only 4 (analytics, billing, platform-admin,
-- review) ever granted anything to hostflow_platform_admin; every other
-- module's migration only wrote "GRANT ... TO hostflow_app". This went
-- unnoticed until PublicPropertyQueries (which deliberately uses
-- platformAdminJdbcTemplate for cross-tenant anonymous marketplace browsing,
-- since there is no tenant context to scope an anonymous request by) failed
-- with "permission denied for table properties" the first time it was
-- actually exercised end-to-end.
--
-- This grants the same privilege set hostflow_app already has, on every
-- existing table, to hostflow_platform_admin -- and sets a default privilege
-- so tables created by future migrations (run as hostflow_migrations) pick it
-- up automatically instead of relying on every module remembering to add it
-- by hand.
DO $$
DECLARE
    tbl RECORD;
BEGIN
    FOR tbl IN SELECT tablename FROM pg_tables WHERE schemaname = 'public' LOOP
        EXECUTE format('GRANT SELECT, INSERT, UPDATE, DELETE ON public.%I TO hostflow_platform_admin', tbl.tablename);
    END LOOP;
END
$$;

ALTER DEFAULT PRIVILEGES FOR ROLE hostflow_migrations IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO hostflow_platform_admin;
