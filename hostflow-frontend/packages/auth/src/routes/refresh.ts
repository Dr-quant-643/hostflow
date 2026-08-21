import { NextRequest, NextResponse } from "next/server";
import { refreshTokens, decodeUserInfo } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import {
  decryptUserSession,
  decryptTokenSession,
  encryptUserSession,
  encryptTokenSession,
  SESSION_COOKIE_NAME,
  TOKEN_COOKIE_NAME,
  sessionCookieOptions,
} from "../session";

// Mount at apps/*/src/app/api/auth/refresh/route.ts as:
//   export { POST } from "@hostflow/auth/routes/refresh";
// Called by getValidAccessToken() (server.ts) when the access token has
// expired — not intended to be called directly by client code.
export async function POST(request: NextRequest) {
  const userRaw = request.cookies.get(SESSION_COOKIE_NAME)?.value;
  const tokenRaw = request.cookies.get(TOKEN_COOKIE_NAME)?.value;
  if (!userRaw || !tokenRaw)
    return NextResponse.json({ error: "no session" }, { status: 401 });

  const userSession = await decryptUserSession(userRaw);
  const tokenSession = await decryptTokenSession(tokenRaw);
  if (!userSession || !tokenSession)
    return NextResponse.json({ error: "invalid session" }, { status: 401 });

  try {
    const tokens = await refreshTokens(tokenSession.refreshToken);
    const claims = decodeJwtPayload(tokens.access_token);
    const userInfo = decodeUserInfo(claims as any);

    // Refreshed tokens can carry updated authorities/scope, so the user
    // cookie gets rewritten too, not just the token cookie.
    const newUserCookie = await encryptUserSession({
      user: {
        id: userInfo.id,
        tenantId: userInfo.tenantId,
        email: userInfo.email,
        name: userInfo.name,
        authorities: userInfo.authorities as any,
        productScope: userInfo.productScope as any,
      },
    });
    const newTokenCookie = await encryptTokenSession({
      accessToken: tokens.access_token,
      refreshToken: tokens.refresh_token, // rotated — old one is now invalid server-side
      accessTokenExpiresAt: Math.floor(Date.now() / 1000) + tokens.expires_in,
    });

    const response = NextResponse.json({ ok: true });
    response.cookies.set(
      SESSION_COOKIE_NAME,
      newUserCookie,
      sessionCookieOptions,
    );
    response.cookies.set(
      TOKEN_COOKIE_NAME,
      newTokenCookie,
      sessionCookieOptions,
    );
    return response;
  } catch {
    // Reuse detection tripped, or Keycloak session revoked — force re-login
    // rather than looping silently.
    const response = NextResponse.json(
      { error: "refresh_failed" },
      { status: 401 },
    );
    response.cookies.delete(SESSION_COOKIE_NAME);
    response.cookies.delete(TOKEN_COOKIE_NAME);
    return response;
  }
}
