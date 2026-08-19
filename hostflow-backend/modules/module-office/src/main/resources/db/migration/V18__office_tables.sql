CREATE TABLE office_meeting_rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    name TEXT NOT NULL,
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE office_room_bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    room_id UUID NOT NULL REFERENCES office_meeting_rooms(id),
    booked_by_user_id UUID NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    purpose TEXT,
    status TEXT NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_room_booking_times CHECK (ends_at > starts_at)
);

CREATE TABLE office_visitors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id),
    hosted_by_user_id UUID NOT NULL,
    full_name TEXT NOT NULL,
    company TEXT,
    expected_at TIMESTAMPTZ NOT NULL,
    checked_in_at TIMESTAMPTZ,
    checked_out_at TIMESTAMPTZ,
    status TEXT NOT NULL DEFAULT 'EXPECTED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_meeting_rooms_tenant_id ON office_meeting_rooms(tenant_id);
CREATE INDEX idx_room_bookings_tenant_id ON office_room_bookings(tenant_id);
CREATE INDEX idx_room_bookings_room_times ON office_room_bookings(room_id, starts_at, ends_at);
CREATE INDEX idx_visitors_tenant_id ON office_visitors(tenant_id);
CREATE INDEX idx_visitors_property_status ON office_visitors(property_id, status);

ALTER TABLE office_meeting_rooms ENABLE ROW LEVEL SECURITY;
ALTER TABLE office_meeting_rooms FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON office_meeting_rooms USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE office_room_bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE office_room_bookings FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON office_room_bookings USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE office_visitors ENABLE ROW LEVEL SECURITY;
ALTER TABLE office_visitors FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON office_visitors USING (tenant_id = current_tenant_id()) WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON office_meeting_rooms TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON office_room_bookings TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON office_visitors TO hostflow_app;
