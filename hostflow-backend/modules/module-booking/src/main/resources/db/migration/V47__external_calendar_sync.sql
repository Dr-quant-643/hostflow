CREATE TABLE external_calendar_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    ics_url TEXT NOT NULL,
    label TEXT,
    last_synced_at TIMESTAMPTZ,
    last_sync_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE external_calendar_blocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    link_id UUID NOT NULL REFERENCES external_calendar_links(id) ON DELETE CASCADE,
    property_id UUID NOT NULL REFERENCES properties(id),
    external_uid TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_calendar_block_link_uid UNIQUE (link_id, external_uid),
    CONSTRAINT chk_calendar_block_dates CHECK (end_date > start_date)
);

CREATE INDEX idx_external_calendar_links_tenant_id ON external_calendar_links(tenant_id);
CREATE INDEX idx_external_calendar_links_property_id ON external_calendar_links(property_id);
CREATE INDEX idx_external_calendar_blocks_tenant_id ON external_calendar_blocks(tenant_id);
CREATE INDEX idx_external_calendar_blocks_property_dates ON external_calendar_blocks(property_id, start_date, end_date);

ALTER TABLE external_calendar_links ENABLE ROW LEVEL SECURITY;
ALTER TABLE external_calendar_links FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON external_calendar_links
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE external_calendar_blocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE external_calendar_blocks FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON external_calendar_blocks
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON external_calendar_links TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON external_calendar_blocks TO hostflow_app;
