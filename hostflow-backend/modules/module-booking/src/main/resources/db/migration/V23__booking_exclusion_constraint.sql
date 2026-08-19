-- THE FIX for the documented race condition (Open Item I, flagged since
-- module-booking's original build): two concurrent requests for overlapping
-- dates on the same property could both pass the application-level
-- availability check before either commits. This constraint makes that
-- structurally impossible — Postgres itself rejects the second INSERT.

CREATE EXTENSION IF NOT EXISTS btree_gist;

-- daterange(check_in, check_out) with default '[)' bounds matches the exact
-- half-open interval semantics already used throughout Booking.overlaps() and
-- BookingRepository.findOverlapping() — same-day checkout/checkin is still
-- correctly NOT treated as an overlap.
--
-- The WHERE clause restricts the exclusion to only PENDING/CONFIRMED/CHECKED_IN
-- bookings (mirrors BookingAvailabilityService.BLOCKING_STATUSES exactly) — a
-- CANCELLED or CHECKED_OUT booking must not block a new booking on the same dates.
ALTER TABLE bookings ADD CONSTRAINT excl_bookings_no_overlap
    EXCLUDE USING gist (
        property_id WITH =,
        daterange(check_in, check_out) WITH &&
    ) WHERE (status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN'));
    