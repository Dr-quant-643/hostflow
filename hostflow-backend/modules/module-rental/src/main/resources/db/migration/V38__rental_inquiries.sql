CREATE TABLE rental_inquiries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    guest_user_id UUID NOT NULL,
    owner_user_id UUID NOT NULL,
    message TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN',
    reply_message TEXT,
    replied_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_rental_inquiries_tenant_id ON rental_inquiries(tenant_id);
CREATE INDEX idx_rental_inquiries_property_id ON rental_inquiries(property_id);
CREATE INDEX idx_rental_inquiries_guest_user_id ON rental_inquiries(guest_user_id);

ALTER TABLE rental_inquiries ENABLE ROW LEVEL SECURITY;
ALTER TABLE rental_inquiries FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON rental_inquiries
    USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON rental_inquiries TO hostflow_app;
