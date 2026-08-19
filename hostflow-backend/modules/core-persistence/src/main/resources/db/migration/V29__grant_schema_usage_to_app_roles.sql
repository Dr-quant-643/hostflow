-- V2 creates hostflow_app/hostflow_migrations/hostflow_platform_admin but never
-- grants USAGE on schema public to them. This worked by accident on a freshly
-- initdb'd database, because Postgres auto-grants USAGE on the initdb-created
-- "public" schema to the PUBLIC pseudo-role. Any environment where "public" was
-- ever dropped and recreated (or a provider that doesn't carry that default)
-- loses that grant, and every query fails with "relation ... does not exist"
-- (schema-invisible, not merely permission-denied) even though the roles have
-- table-level SELECT/INSERT/etc. Making this grant explicit here removes the
-- dependency on that implicit default.
GRANT USAGE ON SCHEMA public TO hostflow_app, hostflow_migrations, hostflow_platform_admin;
