import { NextRequest, NextResponse } from "next/server";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
  base64UrlEncodeString,
} from "../pkce";
import { buildAuthorizationUrl } from "../keycloak-client";
import { oauthFlowCookieName } from "../session";
import { createAuthConfig, type AuthConfig } from "../config";

// Mount per product at e.g. app/xanuos/api/auth/login/route.ts as:
//   export const GET = createLoginRoute(createAuthConfig("XANUOS", "/xanuos"));
export function createLoginRoute(config: AuthConfig) {
  return async function GET(request: NextRequest) {
    const verifier = generateCodeVerifier();
    const challenge = await generateCodeChallenge(verifier);
    const state = generateState();

    // ?idp=google skips Keycloak's own login form and jumps straight to the
    // federated provider — used by the "Continue with Google" button.
    const requestUrl = new URL(request.url);
    const idpHint = requestUrl.searchParams.get("idp") ?? undefined;
    const authUrl = buildAuthorizationUrl(config, { state, codeChallenge: challenge, idpHint });

    // middleware-guard.ts's redirectToLogin sets ?returnTo=<pathname> so a
    // gated page (e.g. NazilCo's /properties/:id/book) sends the user back to
    // where they actually were after login, instead of always landing on the
    // product's generic home page. Packed into the same cookie as
    // state/verifier -- see below for why this can't be a second cookie.
    const returnTo = requestUrl.searchParams.get("returnTo");

    const response = NextResponse.redirect(authUrl);
    // state and verifier are both plain base64url (no "." possible); returnTo
    // is base64url-encoded for the same reason before joining, so a single
    // delimited cookie is safe to split back apart in the callback. Kept as
    // ONE Set-Cookie rather than two/three: this deployment corrupts any
    // response that sets 2+ cookies at once (see session.ts for the full
    // story).
    const cookieValue = returnTo
      ? `${state}.${verifier}.${base64UrlEncodeString(returnTo)}`
      : `${state}.${verifier}`;
    response.cookies.set(oauthFlowCookieName(config.prefix), cookieValue, {
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
