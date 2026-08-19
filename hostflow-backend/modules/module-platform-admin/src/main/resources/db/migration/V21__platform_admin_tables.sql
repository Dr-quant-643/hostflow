-- Not RLS-protected — both tables are platform-wide by design, same as
-- organizations. Access control is enforced entirely at the controller layer
-- (PLATFORM_ADMIN only), not at the database layer.
CREATE TABLE feature_flags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key TEXT NOT NULL,
    scope_org_id UUID,
    enabled BOOLEAN NOT NULL DEFAULT false,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_feature_flag_scope UNIQUE NULLS NOT DISTINCT (key, scope_org_id)
);

CREATE TABLE audit_log_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id UUID NOT NULL,
    actor_tenant_id UUID,
    action TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id TEXT,
    detail TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_feature_flags_key ON feature_flags(key);
CREATE INDEX idx_audit_log_tenant ON audit_log_entries(actor_tenant_id);
CREATE INDEX idx_audit_log_resource_type ON audit_log_entries(resource_type);
CREATE INDEX idx_audit_log_created_at ON audit_log_entries(created_at DESC);

GRANT SELECT, INSERT, UPDATE, DELETE ON feature_flags TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON audit_log_entries TO hostflow_app;
GRANT SELECT ON feature_flags TO hostflow_platform_admin;
GRANT SELECT ON audit_log_entries TO hostflow_platform_admin;
