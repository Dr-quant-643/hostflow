import { NextRequest, NextResponse } from "next/server";
import { exchangeCodeForTokens } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import { decodeUserInfo } from "../keycloak-client";
import { encryptSession, SESSION_COOKIE_NAME, sessionCookieOptions } from "../session";
import { OAUTH_FLOW_COOKIE_NAME } from "./login";

// Mount at apps/*/src/app/api/auth/callback/route.ts as:
//   export { GET } from "@hostflow/auth/routes/callback";
export async function GET(request: NextRequest) {
  const url = new URL(request.url);
  const code = url.searchParams.get("code");
  const state = url.searchParams.get("state");
  const flowCookie = request.cookies.get(OAUTH_FLOW_COOKIE_NAME)?.value;
  const [expectedState, verifier] = flowCookie?.split(".") ?? [];

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

    // Redirects to /api/auth/finish rather than setting the CSRF cookie
    // here directly -- this deployment corrupts any response that sets 2+
    // cookies at once (see session.ts), and this response already needs to
    // both set the session cookie AND clear oauth_flow. One more redirect
    // hop keeps every response down to a single Set-Cookie.
    const response = NextResponse.redirect(new URL("/api/auth/finish", request.url));
    response.cookies.set(SESSION_COOKIE_NAME, sessionCookie, sessionCookieOptions);
    return response;
  } catch {
    return NextResponse.redirect(
      new URL("/?authError=token_exchange_failed", request.url),
    );
  }
}
