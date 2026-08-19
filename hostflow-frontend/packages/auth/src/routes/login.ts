import { NextRequest, NextResponse } from "next/server";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
} from "../pkce";
import { buildAuthorizationUrl } from "../keycloak-client";

// Mount at apps/*/src/app/api/auth/login/route.ts as:
//   export { GET } from "@hostflow/auth/routes/login";
export async function GET(request: NextRequest) {
  const verifier = generateCodeVerifier();
  const challenge = await generateCodeChallenge(verifier);
  const state = generateState();

  const authUrl = buildAuthorizationUrl({ state, codeChallenge: challenge });

  const response = NextResponse.redirect(authUrl);
  // Short-lived, httpOnly — only needed to survive the redirect round-trip.
  response.cookies.set("pkce_verifier", verifier, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 300,
    path: "/",
  });
  response.cookies.set("oauth_state", state, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    maxAge: 300,
    path: "/",
  });
  return response;
}
