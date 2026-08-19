-- Required for gen_random_uuid() used implicitly by Hibernate's UuidGenerator strategy,
-- and pg_trgm for the fuzzy/full-text property search decided in the architecture doc (Phase 1-2).
-- pgvector is intentionally NOT enabled here yet — it belongs to module-property/module-marketing
-- migrations when the recommendation engine is actually built, to keep this migration scoped
-- to core infrastructure only.

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
