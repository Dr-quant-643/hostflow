import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";

const PUBLIC_PATHS = ["/login", "/api/auth", "/access-denied"];

// Gated on PRODUCT_NAZILCO (this is NazilCo's admin app). The Bookings
// oversight screen also calls PlatformAdminController, which requires
// ROLE_PLATFORM_ADMIN server-side — enforced by the backend, not here.
export function middleware(request: NextRequest) {
  return authorityGate(request, {
    publicPaths: PUBLIC_PATHS,
    requireAnyAuthority: ["PRODUCT_NAZILCO"],
  });
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
