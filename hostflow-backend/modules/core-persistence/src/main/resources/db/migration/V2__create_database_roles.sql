-- Implements the BYPASSRLS decision from the architecture reference:
-- Exactly two roles get BYPASSRLS. Nothing else, ever.
--
-- hostflow_migrations      -> used ONLY by Flyway itself to run schema migrations
-- hostflow_platform_admin  -> used ONLY by narrowly-scoped background jobs that
--                             genuinely need cross-tenant access (billing reconciliation,
--                             platform-wide analytics rollups)
-- hostflow_app             -> the normal application runtime role. NEVER gets BYPASSRLS.
--                             Every table's RLS policy (added per-module as each module's
--                             entities are created) is enforced against this role.
--
-- NOTE: CREATE ROLE requires the Flyway connection user to have CREATEROLE privilege
-- (or be a superuser) the first time this runs. In most managed Postgres providers
-- (RDS, Cloud SQL, etc.) the default admin user has this. If this migration fails with
-- a permissions error in your environment, run this file's contents manually once via
-- an admin connection, then re-run `mvn flyway:migrate` (or let the app start normally) —
-- Flyway will record it as applied and continue.

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'hostflow_app') THEN
        CREATE ROLE hostflow_app WITH LOGIN PASSWORD 'okrainc01' NOBYPASSRLS;
    END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'hostflow_migrations') THEN
        CREATE ROLE hostflow_migrations WITH LOGIN PASSWORD 'okrainc02' BYPASSRLS;
    END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'hostflow_platform_admin') THEN
        CREATE ROLE hostflow_platform_admin WITH LOGIN PASSWORD 'okrainc03' BYPASSRLS;
    END IF;
END
$$;

COMMENT ON ROLE hostflow_app IS 'Normal application runtime role. Must never have BYPASSRLS. All tenant-owned tables enforce RLS against this role.';
COMMENT ON ROLE hostflow_migrations IS 'Flyway-only role. Used exclusively for running schema migrations, never by the running application.';
COMMENT ON ROLE hostflow_platform_admin IS 'Cross-tenant background jobs only (billing reconciliation, platform analytics rollups). Must not be used for any user-facing request path.';
