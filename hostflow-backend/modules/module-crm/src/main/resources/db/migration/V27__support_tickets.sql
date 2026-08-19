CREATE TABLE crm_support_tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    contact_id UUID REFERENCES crm_contacts(id),
    raised_by_user_id UUID,
    subject TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN',
    product_scope TEXT NOT NULL,
    assigned_to_user_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_support_tickets_tenant_id ON crm_support_tickets(tenant_id);
CREATE INDEX idx_support_tickets_product_status ON crm_support_tickets(product_scope, status);
CREATE INDEX idx_support_tickets_assigned_to ON crm_support_tickets(assigned_to_user_id);

ALTER TABLE crm_support_tickets ENABLE ROW LEVEL SECURITY;
ALTER TABLE crm_support_tickets FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON crm_support_tickets
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON crm_support_tickets TO hostflow_app;
