CREATE TABLE rental_tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    linked_user_id UUID,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE leases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    tenant_id_ref UUID NOT NULL REFERENCES rental_tenants(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_rent NUMERIC(12,2) NOT NULL,
    security_deposit NUMERIC(12,2),
    status TEXT NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_lease_dates CHECK (end_date > start_date)
);

CREATE TABLE rent_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    lease_id UUID NOT NULL REFERENCES leases(id),
    due_date DATE NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'DUE',
    paid_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_rental_tenants_tenant_id ON rental_tenants(tenant_id);
CREATE INDEX idx_leases_tenant_id ON leases(tenant_id);
CREATE INDEX idx_leases_property_id ON leases(property_id);
CREATE INDEX idx_rent_payments_tenant_id ON rent_payments(tenant_id);
CREATE INDEX idx_rent_payments_lease_id ON rent_payments(lease_id);
CREATE INDEX idx_rent_payments_status_due ON rent_payments(status, due_date);

ALTER TABLE rental_tenants ENABLE ROW LEVEL SECURITY;
ALTER TABLE rental_tenants FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON rental_tenants
    USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE leases ENABLE ROW LEVEL SECURITY;
ALTER TABLE leases FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON leases
    USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE rent_payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE rent_payments FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON rent_payments
    USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON rental_tenants TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON leases TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON rent_payments TO hostflow_app;
