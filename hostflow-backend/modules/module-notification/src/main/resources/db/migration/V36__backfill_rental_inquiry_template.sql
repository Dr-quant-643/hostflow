-- Same reasoning as V34: seedDefaults() only backfills on TENANT_CREATED, so
-- every existing organization needs the new rental_inquiry_owner template
-- inserted directly; seedDefaults() handles every future org going forward.
INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'rental_inquiry_owner', 'EMAIL', 'New rental inquiry on {{property_name}}',
       'A prospective tenant is interested in renting {{property_name}}. Message: {{message}}'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'rental_inquiry_owner'
);
