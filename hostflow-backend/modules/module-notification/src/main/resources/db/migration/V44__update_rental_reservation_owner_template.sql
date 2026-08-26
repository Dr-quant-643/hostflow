-- Reservations no longer auto-activate the lease (see RentalReservationOrchestrator) --
-- the owner now approves or declines it. Existing tenants already have the old
-- "already active" wording seeded by V40; update it in place for all of them.
-- seedIfMissing() only fires on TENANT_CREATED, so it never touches existing rows.
UPDATE notification_templates
SET subject = 'New reservation request on {{property_name}}',
    body = 'A tenant requested to reserve {{property_name}} starting {{move_in_date}} for {{months}} month(s) '
           || 'at {{monthly_rent}}/month. Approve or decline it from your Rental tab.'
WHERE code = 'rental_reservation_owner';
