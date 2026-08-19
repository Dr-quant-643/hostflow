-- Property occupancy summary: joins properties + bookings + invoices across module
-- boundaries at the SQL level only (no Java coupling), consistent with the
-- cross-module UUID-reference convention used throughout this codebase.
CREATE MATERIALIZED VIEW mv_property_occupancy_summary AS
SELECT
    p.id AS property_id,
    p.tenant_id AS tenant_id,
    p.name AS property_name,
    COUNT(DISTINCT b.id) AS total_bookings,
    COALESCE(SUM(b.check_out - b.check_in), 0) AS total_nights_booked,
    COALESCE(SUM(b.total_price) FILTER (WHERE b.status IN ('CHECKED_OUT', 'CONFIRMED', 'CHECKED_IN')), 0) AS total_revenue
FROM properties p
LEFT JOIN bookings b ON b.property_id = p.id
GROUP BY p.id, p.tenant_id, p.name;

CREATE UNIQUE INDEX idx_mv_property_occupancy_property_id ON mv_property_occupancy_summary(property_id);
CREATE INDEX idx_mv_property_occupancy_tenant_id ON mv_property_occupancy_summary(tenant_id);

-- Monthly revenue summary: aggregates invoices by tenant + calendar month. Synthetic
-- string id (tenant_id::text || '-' || month) exists purely so the JPA @Id mapping
-- in MonthlyRevenueSummary.java has a single unique column to bind to.
CREATE MATERIALIZED VIEW mv_monthly_revenue_summary AS
SELECT
    tenant_id::text || '-' || date_trunc('month', due_date)::date::text AS id,
    tenant_id,
    date_trunc('month', due_date)::date AS month,
    SUM(amount) AS invoiced_total,
    SUM(amount) FILTER (WHERE status = 'PAID') AS paid_total,
    COUNT(*) AS invoice_count
FROM invoices
GROUP BY tenant_id, date_trunc('month', due_date);

CREATE UNIQUE INDEX idx_mv_monthly_revenue_id ON mv_monthly_revenue_summary(id);
CREATE INDEX idx_mv_monthly_revenue_tenant_id ON mv_monthly_revenue_summary(tenant_id);

-- NOTE ON RLS: Postgres does NOT support applying ROW LEVEL SECURITY policies
-- directly to materialized views (only to tables). This is WHY
-- PropertyOccupancySummaryRepository/MonthlyRevenueSummaryRepository enforce
-- tenant_id filtering explicitly in their queries, and AnalyticsService's javadoc
-- flags this as the PRIMARY enforcement mechanism, not a redundant layer. This is
-- a deliberate, documented, one-time exception to the RLS-everywhere pattern used
-- for every regular table in this codebase.

-- Refresh function: called on a schedule (a @Scheduled job, to be added in the app
-- module — see this module's report open items) rather than on every write, since
-- materialized views trade real-time accuracy for query speed by design. CONCURRENTLY
-- requires the unique indexes created above, and allows reads to continue against
-- the view uninterrupted while it refreshes.
CREATE OR REPLACE FUNCTION refresh_analytics_views() RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_property_occupancy_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_monthly_revenue_summary;
END;
$$ LANGUAGE plpgsql;

GRANT SELECT ON mv_property_occupancy_summary TO hostflow_app;
GRANT SELECT ON mv_monthly_revenue_summary TO hostflow_app;
GRANT SELECT ON mv_property_occupancy_summary TO hostflow_platform_admin;
GRANT SELECT ON mv_monthly_revenue_summary TO hostflow_platform_admin;
