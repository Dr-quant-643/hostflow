import { NextRequest, NextResponse } from "next/server";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
} from "../pkce";
import { buildAuthorizationUrl } from "../keycloak-client";

export const OAUTH_FLOW_COOKIE_NAME = "oauth_flow";

// Mount at apps/*/src/app/api/auth/login/route.ts as:
//   export { GET } from "@hostflow/auth/routes/login";
export async function GET(request: NextRequest) {
  const verifier = generateCodeVerifier();
  const challenge = await generateCodeChallenge(verifier);
  const state = generateState();

  const authUrl = buildAuthorizationUrl({ state, codeChallenge: challenge });

  const response = NextResponse.redirect(authUrl);
  // state and verifier are both plain base64url (no "." possible), so a
  // single delimited cookie is safe to split back apart in the callback.
  // Kept as ONE Set-Cookie rather than two: this deployment corrupts any
  // response that sets 2+ cookies at once (see session.ts for the full
  // story) -- oauth_state and pkce_verifier used to be separate cookies,
  // and Keycloak's redirect back to /api/auth/callback would arrive with
  // only the first of the two ever actually stored by the browser, so
  // state validation failed on every single login attempt.
  response.cookies.set(OAUTH_FLOW_COOKIE_NAME, `${state}.${verifier}`, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 300,
    path: "/",
  });
  return response;
}
