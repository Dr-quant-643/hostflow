-- No RLS here, same as guest_profiles/organizations — a device token belongs to
-- a person (staff User or NazilCo GuestProfile), identified directly by their
-- Keycloak subject id, not by tenant. Staff and guests share this one table so
-- push delivery doesn't need to know which kind of account it's sending to.
CREATE TABLE device_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    device_token TEXT NOT NULL,
    platform TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_device_tokens_user_token UNIQUE (user_id, device_token)
);

CREATE INDEX idx_device_tokens_user_id_active ON device_tokens(user_id, active);

GRANT SELECT, INSERT, UPDATE, DELETE ON device_tokens TO hostflow_app;
