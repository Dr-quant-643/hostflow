-- Create CRM contacts table
CREATE TABLE IF NOT EXISTS crm_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    assigned_to_user_id UUID,
    last_contacted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for performance
CREATE INDEX idx_crm_contacts_tenant_id ON crm_contacts(tenant_id);
CREATE INDEX idx_crm_contacts_email ON crm_contacts(email);
CREATE INDEX idx_crm_contacts_status ON crm_contacts(status);
CREATE INDEX idx_crm_contacts_assigned_to ON crm_contacts(assigned_to_user_id);
CREATE INDEX idx_crm_contacts_last_contacted ON crm_contacts(last_contacted_at);

-- Add comment for documentation
COMMENT ON TABLE crm_contacts IS 'Customer Relationship Management contacts';
COMMENT ON COLUMN crm_contacts.status IS 'Contact status: ACTIVE, INACTIVE, LEAD, CUSTOMER';
COMMENT ON COLUMN crm_contacts.assigned_to_user_id IS 'User ID of the assigned sales rep';
COMMENT ON COLUMN crm_contacts.last_contacted_at IS 'Last time this contact was contacted';
