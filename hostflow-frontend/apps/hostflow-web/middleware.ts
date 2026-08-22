import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";
import { createAuthConfig } from "@hostflow/auth/src/config";

const xanuosConfig = createAuthConfig("XANUOS", "/xanuos");
const nazilcoConfig = createAuthConfig("NAZILCO", "/nazilco");

// XanuOS: protect-by-default (matches old hostflow-web policy). /xanuos/signup
// is the self-service host-signup form (creates its own Keycloak identity
// server-side, so it must be reachable anonymously, same reasoning as
// NazilCo's /nazilco/signup).
const XANUOS_PUBLIC_PATHS = ["/xanuos/api/auth", "/xanuos/access-denied", "/xanuos/signup"];

// NazilCo: public-by-default (matches old nazilco-web policy) — only these
// paths require a session. Left exactly as the original app had it
// (properties/search/discover stay anonymous-browsable); anything beyond
// this is reconciliation-doc work for after this migration, not part of it.
const NAZILCO_PUBLIC_PATHS = ["/nazilco/api/auth", "/nazilco/access-denied"];
const NAZILCO_PROTECTED_PATHS = [
  "/nazilco/checkout",
  "/nazilco/guest-portal",
  "/nazilco/profile",
  "/nazilco/invoices",
  "/nazilco/notifications",
  "/nazilco/support",
];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Homepage and its picker are always public.
  if (pathname === "/") return NextResponse.next();

  // XanuOS's marketing/landing page is public (property managers should be
  // able to see what the product offers before signing in) even though
  // everything else under /xanuos stays protect-by-default — this is an
  // exact match, not a prefix, since "/xanuos" would otherwise swallow every
  // dashboard route too.
  if (pathname === "/xanuos") return NextResponse.next();

  if (pathname.startsWith("/xanuos")) {
    return authorityGate(
      request,
      { publicPaths: XANUOS_PUBLIC_PATHS, requireAnyAuthority: ["PRODUCT_XANUOS"] },
      xanuosConfig,
    );
  }

  if (pathname.startsWith("/nazilco")) {
    return authorityGate(
      request,
      {
        publicPaths: NAZILCO_PUBLIC_PATHS,
        protectedPaths: NAZILCO_PROTECTED_PATHS,
        requireAnyAuthority: ["PRODUCT_NAZILCO"],
      },
      nazilcoConfig,
    );
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
