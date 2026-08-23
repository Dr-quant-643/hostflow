-- TENANT_CREATED (and other system-initiated) audit events have no human
-- actor by design (TenantEventPublisher.created() passes actorUserId=null).
-- The NOT NULL constraint from V21 made every such insert fail, which
-- poisoned the hostflow.tenant.created RabbitMQ consumer on every org
-- self-signup and wedged the app's DB connection pool for all requests.
ALTER TABLE audit_log_entries ALTER COLUMN actor_user_id DROP NOT NULL;
