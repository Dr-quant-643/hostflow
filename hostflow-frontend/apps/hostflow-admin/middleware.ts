import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";

const PUBLIC_PATHS = ["/login", "/api/auth", "/access-denied"];

// Gated on PRODUCT_XANUOS (this is XanuOS's admin app). Some screens
// (Billing, Bookings-oversight) also call PlatformAdminController endpoints
// that require ROLE_PLATFORM_ADMIN server-side — that stricter check is
// enforced by the backend per-endpoint, not duplicated here.
export function middleware(request: NextRequest) {
  return authorityGate(request, {
    publicPaths: PUBLIC_PATHS,
    requireAnyAuthority: ["PRODUCT_XANUOS"],
  });
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
