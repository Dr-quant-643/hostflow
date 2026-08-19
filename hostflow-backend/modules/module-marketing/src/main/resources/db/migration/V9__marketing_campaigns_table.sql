-- module-marketing's base table was never actually written as a migration —
-- V22__remove__ai_content_generation.sql ALTERs marketing_campaigns (renaming
-- prompt -> content, dropping failure_reason) but no earlier migration ever
-- created it. This reconstructs the pre-V22 shape (prompt/failure_reason
-- present) so V22's ALTERs apply cleanly on top, matching every other
-- module's TenantScopedEntity + RLS pattern.
CREATE TABLE marketing_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID REFERENCES properties(id),
    name TEXT NOT NULL,
    platform TEXT NOT NULL,
    prompt TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'DRAFT',
    failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_campaigns_tenant_id ON marketing_campaigns(tenant_id);
CREATE INDEX idx_marketing_campaigns_property_id ON marketing_campaigns(property_id);

ALTER TABLE marketing_campaigns ENABLE ROW LEVEL SECURITY;
ALTER TABLE marketing_campaigns FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON marketing_campaigns
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON marketing_campaigns TO hostflow_app;
