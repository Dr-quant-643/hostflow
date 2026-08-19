CREATE TABLE maintenance_work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    reported_by_user_id UUID NOT NULL,
    assigned_technician_user_id UUID,
    category TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN',
    resolution_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_work_orders_tenant_id ON maintenance_work_orders(tenant_id);
CREATE INDEX idx_work_orders_property_id ON maintenance_work_orders(property_id);
CREATE INDEX idx_work_orders_status ON maintenance_work_orders(status);
CREATE INDEX idx_work_orders_technician ON maintenance_work_orders(assigned_technician_user_id);

ALTER TABLE maintenance_work_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE maintenance_work_orders FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON maintenance_work_orders
    USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON maintenance_work_orders TO hostflow_app;
