import { NextRequest, NextResponse } from "next/server";
import { generateCsrfToken } from "../csrf";
import { csrfCookieName } from "../session";
import { createAuthConfig, type AuthConfig } from "../config";

// Mount per product at e.g. app/xanuos/api/auth/finish/route.ts as:
//   export const GET = createFinishRoute(createAuthConfig("XANUOS", "/xanuos"));
// Only reachable as the final hop of callback.ts's redirect chain. Exists
// solely to set the CSRF cookie in a response of its own -- see session.ts
// for why no response here ever sets more than one cookie at a time.
export function createFinishRoute(config: AuthConfig) {
  return async function GET(request: NextRequest) {
    const returnTo = new URL(request.url).searchParams.get("returnTo");
    const target = returnTo && returnTo.startsWith(config.basePath) ? returnTo : config.basePath;
    const response = NextResponse.redirect(new URL(target, request.url));
    response.cookies.set(csrfCookieName(config.prefix), generateCsrfToken(), {
      httpOnly: false, // must be readable by client JS to echo back per synchronizer pattern
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      path: "/",
    });
    return response;
  };
}

// Backward-compat bare export — see login.ts for why this is lazy.
export const GET = (request: NextRequest) =>
  createFinishRoute(createAuthConfig())(request);
