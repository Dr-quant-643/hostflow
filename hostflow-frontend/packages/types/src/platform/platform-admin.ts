// Mirrors PlatformBillingQueries / PlatformBookingQueries / PlatformOrganizationQueries
// row projections — all served by PlatformAdminController, hasRole('PLATFORM_ADMIN')
// only. These are deliberately slimmer cross-tenant projections, not the full
// staff-side Invoice/Booking/Organization DTOs.

export interface InvoiceSummaryRow {
  id: string;
  tenantId: string;
  organizationName: string;
  amount: string;
  dueDate: string;
  status: string;
}

export interface BookingOversightRow {
  id: string;
  tenantId: string;
  organizationName: string;
  propertyId: string;
  checkIn: string;
  checkOut: string;
  status: string;
}

export interface OrganizationRow {
  id: string;
  name: string;
  slug: string;
  primaryProduct: string;
  active: boolean;
  createdAt: string;
}

export interface PlatformUserRow {
  id: string;
  tenantId: string;
  organizationName: string;
  email: string;
  firstName: string;
  lastName: string;
  active: boolean;
}

// module-platform-admin's FeatureFlagResponse/SetFeatureFlagRequest.
// scopeOrgId null = the global default; non-null = a per-org override.
export interface FeatureFlagResponse {
  id: string;
  key: string;
  scopeOrgId: string | null;
  enabled: boolean;
  description: string | null;
}

export interface SetFeatureFlagRequest {
  key: string;
  enabled: boolean;
  description?: string;
}

// module-platform-admin's AuditLogResponse (AuditLogEntry).
export interface AuditLogResponse {
  id: string;
  actorUserId: string | null;
  actorTenantId: string | null;
  action: string;
  resourceType: string;
  resourceId: string;
  detail: string | null;
  createdAt: string;
}

// gateway-service's AdminHealthController — proxies app's own
// /actuator/health and re-labels components. NOT wrapped in ApiResponse<T>
// and NOT under /api/v1 (it's a gateway-root endpoint), so the hook that
// fetches this bypasses the normal api-client envelope handling.
export interface ComponentHealth {
  status: string;
  detail: string;
}

export interface AggregatedHealthResponse {
  status: string;
  timestamp: string;
  components: Record<string, ComponentHealth>;
}

// PlatformMonitoringQueries.MonitoringSnapshot — minimal DB-backed business
// metric counts, deliberately not a full metrics pipeline (that's
// Prometheus/Grafana's job per the architecture decision).
export interface MonitoringSnapshot {
  totalOrganizations: number;
  totalXanuosUsers: number;
  totalGuestProfiles: number;
  totalActiveBookings: number;
  totalOpenSupportTickets: number;
  totalOpenWorkOrders: number;
}
