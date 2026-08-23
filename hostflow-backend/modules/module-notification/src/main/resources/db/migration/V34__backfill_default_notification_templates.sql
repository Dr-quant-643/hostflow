-- NotificationTemplateSeedService.seedDefaults() only ever runs from the
-- TENANT_CREATED consumer, so every organization created before this fix
-- never got its default templates -- confirmed empty across every existing
-- tenant. Backfills both templates for all of them; seedDefaults() still
-- handles every future org going forward.
INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'booking_confirmed', 'EMAIL', 'Your booking is confirmed',
       'Hi, your booking is confirmed for check-in {{check_in}} and check-out {{check_out}}. ' ||
       'We look forward to hosting you.'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'booking_confirmed'
);

INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'new_booking_owner', 'EMAIL', 'New booking on {{property_name}}',
       'You have a new booking on {{property_name}} for check-in {{check_in}} and check-out {{check_out}}.'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'new_booking_owner'
);
