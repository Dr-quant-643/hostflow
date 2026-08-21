import { NextRequest, NextResponse } from "next/server";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
} from "../pkce";
import { buildAuthorizationUrl } from "../keycloak-client";
import { oauthFlowCookieName } from "../session";
import { createAuthConfig, type AuthConfig } from "../config";

// Mount per product at e.g. app/xanuos/api/auth/login/route.ts as:
//   export const GET = createLoginRoute(createAuthConfig("XANUOS", "/xanuos"));
export function createLoginRoute(config: AuthConfig) {
  return async function GET(_request: NextRequest) {
    const verifier = generateCodeVerifier();
    const challenge = await generateCodeChallenge(verifier);
    const state = generateState();

    const authUrl = buildAuthorizationUrl(config, { state, codeChallenge: challenge });

    const response = NextResponse.redirect(authUrl);
    // state and verifier are both plain base64url (no "." possible), so a
    // single delimited cookie is safe to split back apart in the callback.
    // Kept as ONE Set-Cookie rather than two: this deployment corrupts any
    // response that sets 2+ cookies at once (see session.ts for the full
    // story).
    response.cookies.set(oauthFlowCookieName(config.prefix), `${state}.${verifier}`, {
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      maxAge: 300,
      path: "/",
    });
    return response;
  };
}

// Backward-compat bare export — xanuos-console (untouched by the 5-to-3 app
// migration) still mounts this as `export { GET } from ".../login"` with no
// config, so it gets the default unprefixed identity (createAuthConfig()).
// Constructed lazily, per-request: this module is also imported by the
// merged apps purely for createLoginRoute, and eagerly building a config at
// module-load time would throw for them (they only set prefixed env vars,
// never the bare KEYCLOAK_CLIENT_ID etc. this default config reads).
export const GET = (request: NextRequest) =>
  createLoginRoute(createAuthConfig())(request);
