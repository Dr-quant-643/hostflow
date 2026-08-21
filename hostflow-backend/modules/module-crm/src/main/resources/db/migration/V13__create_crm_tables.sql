-- Matches entity.Contact (the live CRM contact model) -- a Contact is a
-- single full_name + optional linked_user_id, not a first/last name split.
-- An earlier draft of this migration modeled a fuller contact (first/last
-- name, notes, assigned rep, last-contacted) that was never implemented;
-- module-crm's actual Contact/ContactService/ContactController never
-- referenced those columns, so this never worked end-to-end until now.
CREATE TABLE IF NOT EXISTS crm_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    linked_user_id UUID,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    source VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'LEAD',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_crm_contacts_tenant_id ON crm_contacts(tenant_id);
CREATE INDEX idx_crm_contacts_email ON crm_contacts(email);
CREATE INDEX idx_crm_contacts_status ON crm_contacts(status);

COMMENT ON TABLE crm_contacts IS 'Customer Relationship Management contacts';
COMMENT ON COLUMN crm_contacts.status IS 'Contact status: LEAD, QUALIFIED, CUSTOMER, LOST';
COMMENT ON COLUMN crm_contacts.linked_user_id IS 'Set once a contact converts to a real User account';
