// Dev/test-only tenant header per the integration guide: "In dev/test, use
// X-Tenant-ID if JWT tenant claim is absent." Production must rely on the
// JWT claim resolved server-side — this module never sends the header
// outside development.

let devTenantId: string | null = null;

export function setDevTenantId(tenantId: string | null) {
  devTenantId = tenantId;
}

export function tenantHeaders(): Record<string, string> {
  const isDev =
    typeof process !== "undefined" && process.env.NODE_ENV !== "production";
  if (isDev && devTenantId) {
    return { "X-Tenant-ID": devTenantId };
  }
  return {};
}
