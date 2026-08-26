CREATE TABLE segment_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    target_segment TEXT NOT NULL,
    subject TEXT NOT NULL,
    body TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'DRAFT',
    recipient_count INTEGER,
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_segment_campaigns_tenant_id ON segment_campaigns(tenant_id);

ALTER TABLE segment_campaigns ENABLE ROW LEVEL SECURITY;
ALTER TABLE segment_campaigns FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON segment_campaigns
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON segment_campaigns TO hostflow_app;
