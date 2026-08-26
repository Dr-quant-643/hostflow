-- Owner-approval workflow: a PENDING booking can now be declined instead of
-- only confirmed/cancelled, with a reason the guest sees on their status card.
ALTER TABLE bookings ADD COLUMN decline_reason TEXT;
