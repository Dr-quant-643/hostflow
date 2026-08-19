CREATE TABLE maintenance_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    name TEXT NOT NULL,
    category TEXT,
    serial_number TEXT,
    purchase_date DATE,
    warranty_expiry_date DATE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE maintenance_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    asset_id UUID REFERENCES maintenance_assets(id),
    category TEXT NOT NULL,
    title TEXT NOT NULL,
    interval_days INTEGER NOT NULL,
    next_due_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Allow NULL reported_by_user_id for auto-generated preventive maintenance work
-- orders (no human reporter for a system-generated order).
ALTER TABLE maintenance_work_orders ALTER COLUMN reported_by_user_id DROP NOT NULL;

CREATE INDEX idx_maintenance_assets_tenant_id ON maintenance_assets(tenant_id);
CREATE INDEX idx_maintenance_assets_property_id ON maintenance_assets(property_id);
CREATE INDEX idx_maintenance_schedules_tenant_id ON maintenance_schedules(tenant_id);
CREATE INDEX idx_maintenance_schedules_due ON maintenance_schedules(active, next_due_date);

ALTER TABLE maintenance_assets ENABLE ROW LEVEL SECURITY;
ALTER TABLE maintenance_assets FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON maintenance_assets USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE maintenance_schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE maintenance_schedules FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON maintenance_schedules USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON maintenance_assets TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON maintenance_schedules TO hostflow_app;
