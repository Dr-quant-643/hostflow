-- V13 (rewritten in place after crm_contacts' original schema turned out to
-- never match the live Contact entity) recreated crm_contacts but, unlike
-- every other module's table migration, never granted hostflow_app table
-- privileges or enabled row-level security on it -- caught because
-- ContactRepository queries would otherwise fail with "permission denied"
-- the first time they actually ran. V13 itself can't be edited again
-- without re-triggering a Flyway checksum mismatch (it already applied), so
-- this is a follow-up migration instead.
ALTER TABLE crm_contacts ENABLE ROW LEVEL SECURITY;
ALTER TABLE crm_contacts FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON crm_contacts
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON crm_contacts TO hostflow_app;

-- crm_support_tickets' contact_id FK was dropped along with the old
-- crm_contacts table (CASCADE) when it was reset, and can't be recreated by
-- re-running V27 (already applied, and its CREATE TABLE isn't idempotent).
ALTER TABLE crm_support_tickets
    ADD CONSTRAINT crm_support_tickets_contact_id_fkey FOREIGN KEY (contact_id) REFERENCES crm_contacts(id);

-- entity.Interaction has never had a matching migration -- an append-only
-- interaction log (calls, emails, notes) referenced by ContactController/
-- ContactService/SupportTicketService, but nothing ever created its table.
CREATE TABLE crm_interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    contact_id UUID NOT NULL REFERENCES crm_contacts(id),
    logged_by_user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_crm_interactions_tenant_id ON crm_interactions(tenant_id);
CREATE INDEX idx_crm_interactions_contact_id ON crm_interactions(contact_id);

ALTER TABLE crm_interactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE crm_interactions FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON crm_interactions
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON crm_interactions TO hostflow_app;

COMMENT ON TABLE crm_interactions IS 'Append-only CRM interaction history (calls, emails, notes, etc.)';
