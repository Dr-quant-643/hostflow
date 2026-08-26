-- Owner-entered unit occupancy (e.g. "6 of 10 units occupied") for
-- multi-unit properties -- NOT derived from Booking/Lease data. The
-- booking system's excl_bookings_no_overlap constraint (V23) makes a
-- property structurally single-booking-at-a-time, so there is no live
-- per-unit inventory to derive this from yet; the owner reports it
-- directly, same pattern as manual_occupied_until.
ALTER TABLE properties
    ADD COLUMN total_units INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN occupied_units INTEGER NOT NULL DEFAULT 0;
