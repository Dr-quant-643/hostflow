-- Same reasoning as V34/V36/V39: seedDefaults() only backfills on TENANT_CREATED,
-- so every existing organization needs the new rental_reservation_owner
-- template inserted directly; seedDefaults() handles every future org.
INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'rental_reservation_owner', 'EMAIL', 'New reservation on {{property_name}}',
       'A tenant reserved {{property_name}} starting {{move_in_date}} for {{months}} month(s) '
       || 'at {{monthly_rent}}/month. The lease is already active in your Rental tab.'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'rental_reservation_owner'
);
