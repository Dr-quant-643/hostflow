-- =====================================================
-- ANALYTICS MATERIALIZED VIEWS (CORRECTED VERSION)
-- =====================================================

-- 1. Monthly Revenue Summary View
-- Shows revenue by tenant and month
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_monthly_revenue_summary AS
SELECT 
    tenant_id,
    DATE_TRUNC('month', created_at) as month,
    COUNT(*) as invoice_count,
    SUM(amount) as total_revenue,
    SUM(CASE WHEN status = 'PAID' THEN amount ELSE 0 END) as paid_amount
FROM invoices
GROUP BY tenant_id, DATE_TRUNC('month', created_at);

-- 2. Property Occupancy Summary View
-- Shows occupancy rates by property
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_property_occupancy_summary AS
SELECT 
    p.id as property_id,
    p.tenant_id,
    p.name as property_name,
    COUNT(b.id) as total_bookings,
    SUM(CASE WHEN b.status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed_bookings,
    SUM(CASE WHEN b.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_bookings,
    SUM(CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_bookings,
    SUM(b.total_price) as total_revenue
FROM properties p
LEFT JOIN bookings b ON p.id = b.property_id
GROUP BY p.id, p.tenant_id, p.name;

-- 3. Booking Analytics View
-- Daily booking statistics using actual column names
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_booking_analytics AS
SELECT 
    DATE_TRUNC('day', created_at) as booking_date,
    tenant_id,
    COUNT(*) as total_bookings,
    COUNT(*) FILTER (WHERE status = 'CONFIRMED') as confirmed_bookings,
    COUNT(*) FILTER (WHERE status = 'CANCELLED') as cancelled_bookings,
    COUNT(*) FILTER (WHERE status = 'PENDING') as pending_bookings,
    COUNT(*) FILTER (WHERE status = 'COMPLETED') as completed_bookings,
    AVG(total_price) as avg_booking_value,
    SUM(total_price) as total_booking_revenue
FROM bookings
GROUP BY DATE_TRUNC('day', created_at), tenant_id;

-- =====================================================
-- CREATE INDEXES FOR PERFORMANCE
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_mv_monthly_revenue_tenant ON mv_monthly_revenue_summary(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mv_monthly_revenue_month ON mv_monthly_revenue_summary(month);

CREATE INDEX IF NOT EXISTS idx_mv_property_occupancy_tenant ON mv_property_occupancy_summary(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mv_property_occupancy_property ON mv_property_occupancy_summary(property_id);

CREATE INDEX IF NOT EXISTS idx_mv_booking_analytics_date ON mv_booking_analytics(booking_date);
CREATE INDEX IF NOT EXISTS idx_mv_booking_analytics_tenant ON mv_booking_analytics(tenant_id);

-- =====================================================
-- REFRESH FUNCTION (WITH CONCURRENTLY)
-- =====================================================

CREATE OR REPLACE FUNCTION refresh_analytics_views()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_monthly_revenue_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_property_occupancy_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_booking_analytics;
    
    RAISE NOTICE 'All analytics views refreshed successfully at %', NOW();
EXCEPTION
    WHEN OTHERS THEN
        RAISE WARNING 'Failed to refresh analytics views: %', SQLERRM;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- GRANT PERMISSIONS
-- =====================================================

GRANT SELECT ON mv_monthly_revenue_summary TO hostflow_app;
GRANT SELECT ON mv_property_occupancy_summary TO hostflow_app;
GRANT SELECT ON mv_booking_analytics TO hostflow_app;

GRANT EXECUTE ON FUNCTION refresh_analytics_views() TO hostflow_app;
GRANT EXECUTE ON FUNCTION refresh_analytics_views() TO hostflow_migrations;

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON MATERIALIZED VIEW mv_monthly_revenue_summary IS 'Monthly revenue aggregated by tenant from invoices';
COMMENT ON MATERIALIZED VIEW mv_property_occupancy_summary IS 'Property occupancy rates and booking statistics';
COMMENT ON MATERIALIZED VIEW mv_booking_analytics IS 'Daily booking analytics by tenant';
COMMENT ON FUNCTION refresh_analytics_views() IS 'Refreshes all analytics materialized views concurrently';
