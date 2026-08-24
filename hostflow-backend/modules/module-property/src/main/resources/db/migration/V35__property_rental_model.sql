-- Backfilled default is NIGHTLY for existing rows only; the app layer
-- (CreatePropertyRequest) requires an explicit value for every new property
-- going forward, since it's a judgment call only the owner can make.
ALTER TABLE properties ADD COLUMN rental_model TEXT NOT NULL DEFAULT 'NIGHTLY';
