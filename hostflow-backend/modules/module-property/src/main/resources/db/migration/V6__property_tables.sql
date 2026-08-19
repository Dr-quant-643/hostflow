-- pgvector enabled HERE, not in core-persistence's V1 — deliberately scoped to the
-- module that first needs it, per the decision to move pgvector to Phase 1 for the
-- recommendation engine while keeping core-persistence's migrations infra-only.
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    owner_user_id UUID NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    property_type TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'DRAFT',
    address_line TEXT NOT NULL,
    city TEXT NOT NULL,
    country TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    base_price NUMERIC(12,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE property_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    object_key TEXT NOT NULL,
    file_name TEXT NOT NULL,
    content_type TEXT NOT NULL,
    document_type TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

-- Reference table for AI recommendation embeddings, kept separate from `properties`
-- itself (see Property.java's javadoc: embedding regeneration is independent of the
-- core property record's own version/updatedAt).
CREATE TABLE property_embeddings (
    property_id UUID PRIMARY KEY REFERENCES properties(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES organizations(id),
    embedding vector(1536),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_properties_tenant_id ON properties(tenant_id);
CREATE INDEX idx_properties_status ON properties(status);
CREATE INDEX idx_properties_owner ON properties(owner_user_id);
CREATE INDEX idx_property_documents_property_id ON property_documents(property_id);
-- ivfflat index for approximate nearest-neighbor search on embeddings; lists=100 is a
-- reasonable Phase 1 default for a moderate row count, revisit once real volume exists.
CREATE INDEX idx_property_embeddings_vector ON property_embeddings
    USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

ALTER TABLE properties ENABLE ROW LEVEL SECURITY;
ALTER TABLE properties FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON properties
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE property_documents ENABLE ROW LEVEL SECURITY;
ALTER TABLE property_documents FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON property_documents
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

ALTER TABLE property_embeddings ENABLE ROW LEVEL SECURITY;
ALTER TABLE property_embeddings FORCE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON property_embeddings
    USING (tenant_id = current_tenant_id())
    WITH CHECK (tenant_id = current_tenant_id());

GRANT SELECT, INSERT, UPDATE, DELETE ON properties TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON property_documents TO hostflow_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON property_embeddings TO hostflow_app;
