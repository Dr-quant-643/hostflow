import { NextRequest, NextResponse } from "next/server";
import { exchangeCodeForTokens } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import { decodeUserInfo } from "../keycloak-client";
import {
  encryptSession,
  SESSION_COOKIE_NAME,
  sessionCookieOptions,
} from "../session";
import { generateCsrfToken, CSRF_COOKIE_NAME } from "../csrf";

// Mount at apps/*/src/app/api/auth/callback/route.ts as:
//   export { GET } from "@hostflow/auth/routes/callback";
export async function GET(request: NextRequest) {
  const url = new URL(request.url);
  const code = url.searchParams.get("code");
  const state = url.searchParams.get("state");
  const expectedState = request.cookies.get("oauth_state")?.value;
  const verifier = request.cookies.get("pkce_verifier")?.value;

  if (!code || !state || !verifier || state !== expectedState) {
    return NextResponse.redirect(
      new URL("/?authError=invalid_state", request.url),
    );
  }

  try {
    const tokens = await exchangeCodeForTokens(code, verifier);
    const claims = decodeJwtPayload(tokens.access_token);
    const userInfo = decodeUserInfo(claims as any);

    const sessionCookie = await encryptSession({
      user: {
        id: userInfo.id,
        tenantId: userInfo.tenantId,
        email: userInfo.email,
        name: userInfo.name,
        authorities: userInfo.authorities as any,
        productScope: userInfo.productScope as any,
      },
      accessToken: tokens.access_token,
      refreshToken: tokens.refresh_token,
      accessTokenExpiresAt: Math.floor(Date.now() / 1000) + tokens.expires_in,
    });

    const response = NextResponse.redirect(new URL("/", request.url));
    response.cookies.set(
      SESSION_COOKIE_NAME,
      sessionCookie,
      sessionCookieOptions,
    );
    response.cookies.set(CSRF_COOKIE_NAME, generateCsrfToken(), {
      httpOnly: false, // must be readable by client JS to echo back per synchronizer pattern
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      path: "/",
    });
    response.cookies.delete("pkce_verifier");
    response.cookies.delete("oauth_state");
    return response;
  } catch {
    return NextResponse.redirect(
      new URL("/?authError=token_exchange_failed", request.url),
    );
  }
}
