import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { decryptSession, SESSION_COOKIE_NAME } from "./session";
import type { Authority } from "@hostflow/types";

// Shared middleware-level auth gate for all 5 apps. Runs in the Edge
// runtime, so it can't use next/headers' cookies()/getServerSession() (that
// only works in Server Components/Route Handlers) — it reads the session
// cookie directly off NextRequest and reuses decryptSession, which is
// Edge-safe (jose, not Node's crypto).
//
// Every app before this had session-presence-only gating (any authenticated
// user of ANY app could reach any other app's pages — a NazilCo guest
// session cookie would pass hostflow-admin's or xanuos-console's middleware
// equally well, since neither checked which product/role the session
// actually carried). This adds the missing authority check.
export interface AuthorityGateOptions {
  /** Path prefixes that bypass auth entirely (login/callback routes, etc). */
  publicPaths?: string[];
  /**
   * Path prefixes that require auth+authority. If omitted, every non-public
   * path requires it (protect-by-default — the admin/console apps). If set,
   * only matching paths are gated and everything else passes through
   * (public-by-default — nazilco-web's anonymous browsing).
   */
  protectedPaths?: string[];
  /** User must hold at least one of these authorities to proceed. */
  requireAnyAuthority: Authority[];
}

function redirectToLogin(request: NextRequest, pathname: string): NextResponse {
  const loginUrl = new URL("/api/auth/login", request.url);
  loginUrl.searchParams.set("returnTo", pathname);
  return NextResponse.redirect(loginUrl);
}

// NOT "/" — in the protect-by-default apps (everything but nazilco-web),
// "/" itself requires the same authority, so redirecting a rejected user
// back to "/" is an infinite redirect loop. /access-denied must be listed
// in each app's publicPaths.
function redirectForbidden(request: NextRequest): NextResponse {
  return NextResponse.redirect(new URL("/access-denied", request.url));
}

export async function authorityGate(
  request: NextRequest,
  options: AuthorityGateOptions,
): Promise<NextResponse> {
  const { pathname } = request.nextUrl;
  const { publicPaths = [], protectedPaths, requireAnyAuthority } = options;

  if (publicPaths.some((p) => pathname.startsWith(p))) {
    return NextResponse.next();
  }
  if (protectedPaths && !protectedPaths.some((p) => pathname.startsWith(p))) {
    return NextResponse.next();
  }

  // TEMPORARY dev-only preview aid — see packages/auth/src/server.ts. Never
  // true unless explicitly set in .env.local. Mock authorities still go
  // through the real authority check below (rather than skipping it
  // outright) so each app's .env.local mock role/product-scope stays
  // meaningful during preview.
  if (process.env.DEV_MOCK_AUTH === "true") {
    const mockAuthorities = (process.env.DEV_MOCK_AUTHORITIES ?? "")
      .split(",")
      .map((a) => a.trim());
    const ok = requireAnyAuthority.some((a) => mockAuthorities.includes(a));
    return ok ? NextResponse.next() : redirectForbidden(request);
  }

  const raw = request.cookies.get(SESSION_COOKIE_NAME)?.value;
  if (!raw) {
    return redirectToLogin(request, pathname);
  }

  const session = await decryptSession(raw);
  if (!session) {
    return redirectToLogin(request, pathname);
  }

  const authorities = session.user.authorities ?? [];
  if (!requireAnyAuthority.some((a) => authorities.includes(a))) {
    return redirectForbidden(request);
  }

  return NextResponse.next();
}
