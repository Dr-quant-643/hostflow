-- Owner-approval workflow for self-service reservations: a DRAFT lease can
-- now be declined instead of only activated, with a reason the guest sees
-- on their status card.
ALTER TABLE leases ADD COLUMN decline_reason TEXT;
