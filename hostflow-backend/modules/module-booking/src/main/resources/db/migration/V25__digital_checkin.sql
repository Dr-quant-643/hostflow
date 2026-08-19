CREATE TABLE booking_digital_checkins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    booking_id UUID NOT NULL UNIQUE REFERENCES bookings(id),
    id_document_object_key TEXT,
    confirmed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_digital_checkins_tenant_id ON booking_digital_checkins(tenant_id);

ALTER TABLE booking_digital_checkins ENABLE ROW LEVEL SECURITY;
ALTER TABLE booking_digital_checkins FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON booking_digital_checkins USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON booking_digital_checkins TO hostflow_app;
