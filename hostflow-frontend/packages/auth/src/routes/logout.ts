import { NextRequest, NextResponse } from "next/server";
import { createAuthConfig, type AuthConfig } from "../config";

// Mount per product at e.g. app/xanuos/api/auth/logout/route.ts as:
//   export const POST = createLogoutRoute(createAuthConfig("XANUOS", "/xanuos"));
// Only clears the session cookie here and hands off to logout-finish for
// the CSRF cookie -- see session.ts for why no response here ever clears
// more than one cookie at a time.
export function createLogoutRoute(config: AuthConfig) {
  return async function POST(request: NextRequest) {
    const response = NextResponse.redirect(
      new URL(`${config.basePath}/api/auth/logout-finish`, request.url),
    );
    response.cookies.delete(config.sessionCookieName);
    return response;
  };
}

// Backward-compat bare export — see login.ts for why this is lazy.
export const POST = (request: NextRequest) =>
  createLogoutRoute(createAuthConfig())(request);
