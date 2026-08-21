import { NextRequest, NextResponse } from "next/server";
import { buildLogoutUrl } from "../keycloak-client";
import { csrfCookieName } from "../session";
import { createAuthConfig, type AuthConfig } from "../config";

// Mount per product at e.g. app/xanuos/api/auth/logout-finish/route.ts as:
//   export const GET = createLogoutFinishRoute(createAuthConfig("XANUOS", "/xanuos"));
// Only reachable as the second hop of logout.ts's redirect chain. Clears
// the CSRF cookie, then hands off to Keycloak to end the actual SSO
// session for this product.
export function createLogoutFinishRoute(config: AuthConfig) {
  return async function GET(_request: NextRequest) {
    const response = NextResponse.redirect(buildLogoutUrl(config));
    response.cookies.delete(csrfCookieName(config.prefix));
    return response;
  };
}

// Backward-compat bare export — see login.ts for why this is lazy.
export const GET = (request: NextRequest) =>
  createLogoutFinishRoute(createAuthConfig())(request);
