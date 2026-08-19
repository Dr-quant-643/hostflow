import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";

const PUBLIC_PATHS = ["/login", "/api/auth", "/access-denied"];

// Every PlatformAdminController/OrganizationController/FeatureFlagController/
// AuditLogController/PlatformMonitoringController endpoint this app talks to
// is hasRole('PLATFORM_ADMIN') only — never PRODUCT_XANUOS or PRODUCT_NAZILCO
// alone, since those say nothing about cross-tenant access. This was the
// single most load-bearing missing gate in the whole frontend: without it,
// any authenticated session from any of the other 4 apps could reach this
// console equally well.
export function middleware(request: NextRequest) {
  return authorityGate(request, {
    publicPaths: PUBLIC_PATHS,
    requireAnyAuthority: ["ROLE_PLATFORM_ADMIN"],
  });
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
