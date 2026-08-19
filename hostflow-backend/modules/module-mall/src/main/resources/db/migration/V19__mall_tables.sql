CREATE TABLE mall_retail_units (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    unit_number TEXT NOT NULL,
    size_sqm NUMERIC(10,2),
    status TEXT NOT NULL DEFAULT 'VACANT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE mall_retail_tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    retail_unit_id UUID NOT NULL REFERENCES mall_retail_units(id),
    business_name TEXT NOT NULL,
    contact_email TEXT,
    contact_phone TEXT,
    monthly_rent NUMERIC(12,2) NOT NULL,
    revenue_share_percent NUMERIC(5,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE mall_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    title TEXT NOT NULL,
    description TEXT,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE mall_parking_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    vehicle_plate TEXT NOT NULL,
    entered_at TIMESTAMPTZ NOT NULL,
    exited_at TIMESTAMPTZ,
    fee_charged NUMERIC(10,2),
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_retail_units_tenant_id ON mall_retail_units(tenant_id);
CREATE INDEX idx_retail_tenants_tenant_id ON mall_retail_tenants(tenant_id);
CREATE INDEX idx_mall_events_tenant_id ON mall_events(tenant_id);
CREATE INDEX idx_parking_sessions_tenant_id ON mall_parking_sessions(tenant_id);
CREATE INDEX idx_parking_sessions_active_lookup ON mall_parking_sessions(property_id, vehicle_plate, status);

ALTER TABLE mall_retail_units ENABLE ROW LEVEL SECURITY;
ALTER TABLE mall_retail_units FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON mall_retail_units USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE mall_retail_tenants ENABLE ROW LEVEL SECURITY;
ALTER TABLE mall_retail_tenants FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON mall_retail_tenants USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE mall_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE mall_events FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON mall_events USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE mall_parking_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE mall_parking_sessions FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON mall_parking_sessions USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON mall_retail_units TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON mall_retail_tenants TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON mall_events TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON mall_parking_sessions TO hostflow_app;
