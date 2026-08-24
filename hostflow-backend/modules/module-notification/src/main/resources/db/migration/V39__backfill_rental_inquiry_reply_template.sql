-- Same reasoning as V34/V36: seedDefaults() only backfills on TENANT_CREATED,
-- so every existing organization needs the new rental_inquiry_reply_guest
-- template inserted directly; seedDefaults() handles every future org.
INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'rental_inquiry_reply_guest', 'EMAIL', 'The owner replied about {{property_name}}',
       'The owner of {{property_name}} replied to your inquiry: {{reply_message}}'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'rental_inquiry_reply_guest'
);
