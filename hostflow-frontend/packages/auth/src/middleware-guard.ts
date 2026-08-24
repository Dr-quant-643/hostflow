import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { decryptSession } from "./session";
import { createAuthConfig, type AuthConfig } from "./config";
import type { Authority } from "@hostflow/types";

// Shared middleware-level auth gate. Runs in the Edge runtime, so it can't
// use next/headers' cookies()/getServerSession() (that only works in Server
// Components/Route Handlers) — it reads the session cookie directly off
// NextRequest and reuses decryptSession, which is Edge-safe (jose, not
// Node's crypto).
//
// Before this existed, every app had session-presence-only gating (any
// authenticated user of ANY app could reach any other app's pages — a
// NazilCo guest session cookie would pass hostflow-admin's or
// xanuos-console's middleware equally well, since neither checked which
// product/role the session actually carried). This adds the missing
// authority check.
//
// `config` is optional and defaults to the unprefixed identity
// (createAuthConfig()) — xanuos-console (untouched by the 5-to-3 app
// migration) calls this with no config and keeps working exactly as
// before. The merged apps (hostflow-web hosting both XanuOS and NazilCo,
// hostflow-admin hosting both admin workspaces) call this once per product
// branch with that product's own config, so each branch checks its own
// cookie name/secret and redirects to its own basePath's login/access-denied.
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
  /**
   * Path suffixes that require auth+authority, checked in addition to
   * protectedPaths. Exists for dynamic routes where a prefix would also
   * swallow a sibling public page -- e.g. NazilCo's
   * /properties/:id/book must be gated while /properties/:id itself (the
   * anonymous-browsable detail page) stays public, so a prefix match on
   * /properties isn't usable here.
   */
  protectedPathSuffixes?: string[];
  /** User must hold at least one of these authorities to proceed. */
  requireAnyAuthority: Authority[];
}

function redirectToLogin(
  request: NextRequest,
  pathname: string,
  config: AuthConfig,
): NextResponse {
  const loginUrl = new URL(`${config.basePath}/api/auth/login`, request.url);
  loginUrl.searchParams.set("returnTo", pathname);
  return NextResponse.redirect(loginUrl);
}

// NOT the product root — in protect-by-default branches, the root itself
// requires the same authority, so redirecting a rejected user back to it is
// an infinite redirect loop. ${basePath}/access-denied must be listed in
// that branch's publicPaths.
function redirectForbidden(request: NextRequest, config: AuthConfig): NextResponse {
  return NextResponse.redirect(new URL(`${config.basePath}/access-denied`, request.url));
}

export async function authorityGate(
  request: NextRequest,
  options: AuthorityGateOptions,
  config: AuthConfig = createAuthConfig(),
): Promise<NextResponse> {
  const { pathname } = request.nextUrl;
  const { publicPaths = [], protectedPaths, protectedPathSuffixes, requireAnyAuthority } = options;

  if (publicPaths.some((p) => pathname.startsWith(p))) {
    return NextResponse.next();
  }
  if (protectedPaths || protectedPathSuffixes) {
    const matches =
      (protectedPaths?.some((p) => pathname.startsWith(p)) ?? false) ||
      (protectedPathSuffixes?.some((s) => pathname.endsWith(s)) ?? false);
    if (!matches) {
      return NextResponse.next();
    }
  }

  // TEMPORARY dev-only preview aid — see packages/auth/src/server.ts. Never
  // true unless explicitly set in .env.local. Mock authorities still go
  // through the real authority check below (rather than skipping it
  // outright) so each app's .env.local mock role/product-scope stays
  // meaningful during preview.
  const mockAuthEnv = config.prefix ? `${config.prefix}_DEV_MOCK_AUTH` : "DEV_MOCK_AUTH";
  const mockAuthoritiesEnv = config.prefix
    ? `${config.prefix}_DEV_MOCK_AUTHORITIES`
    : "DEV_MOCK_AUTHORITIES";
  if (process.env[mockAuthEnv] === "true") {
    const mockAuthorities = (process.env[mockAuthoritiesEnv] ?? "")
      .split(",")
      .map((a) => a.trim());
    const ok = requireAnyAuthority.some((a) => mockAuthorities.includes(a));
    return ok ? NextResponse.next() : redirectForbidden(request, config);
  }

  const raw = request.cookies.get(config.sessionCookieName)?.value;
  if (!raw) {
    return redirectToLogin(request, pathname, config);
  }

  const session = await decryptSession(raw, config.sessionSecret);
  if (!session) {
    return redirectToLogin(request, pathname, config);
  }

  const authorities = session.user.authorities ?? [];
  if (!requireAnyAuthority.some((a) => authorities.includes(a))) {
    return redirectForbidden(request, config);
  }

  return NextResponse.next();
}
