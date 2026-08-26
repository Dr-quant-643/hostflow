-- Same reasoning as V34/V36/V39/V40: seedDefaults() only backfills on
-- TENANT_CREATED, so every existing organization needs the new
-- maintenance_request_owner template inserted directly.
INSERT INTO notification_templates (tenant_id, code, channel, subject, body)
SELECT o.id, 'maintenance_request_owner', 'EMAIL', 'New maintenance issue on {{property_name}}',
       'A tenant reported a maintenance issue on {{property_name}}: {{title}}. '
       || 'See it in your Maintenance tab.'
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates t
    WHERE t.tenant_id = o.id AND t.code = 'maintenance_request_owner'
);
