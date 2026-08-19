-- Removes the AI content-generation apparatus entirely, per decision to drop this
-- feature rather than build it. Campaigns are now simple content records.
DROP TABLE IF EXISTS marketing_generated_content;

ALTER TABLE marketing_campaigns RENAME COLUMN prompt TO content;
ALTER TABLE marketing_campaigns DROP COLUMN IF EXISTS failure_reason;
ALTER TABLE marketing_campaigns ALTER COLUMN status SET DEFAULT 'DRAFT';

-- Any campaigns left mid-generation from before this migration are moved to DRAFT
-- so they don't sit in a status value that no longer exists in the Java enum.
UPDATE marketing_campaigns SET status = 'DRAFT' WHERE status IN ('GENERATING', 'GENERATION_FAILED');
UPDATE marketing_campaigns SET status = 'PUBLISHED' WHERE status = 'READY';
