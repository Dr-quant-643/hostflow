import { NextRequest, NextResponse } from "next/server";
import { refreshTokens, decodeUserInfo } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import {
  decryptSession,
  encryptSession,
  SESSION_COOKIE_NAME,
  sessionCookieOptions,
} from "../session";

// Mount at apps/*/src/app/api/auth/refresh/route.ts as:
//   export { POST } from "@hostflow/auth/routes/refresh";
// Called by getValidAccessToken() (server.ts) when the access token has
// expired — not intended to be called directly by client code.
export async function POST(request: NextRequest) {
  const raw = request.cookies.get(SESSION_COOKIE_NAME)?.value;
  if (!raw) return NextResponse.json({ error: "no session" }, { status: 401 });

  const session = await decryptSession(raw);
  if (!session)
    return NextResponse.json({ error: "invalid session" }, { status: 401 });

  try {
    const tokens = await refreshTokens(session.refreshToken);
    const claims = decodeJwtPayload(tokens.access_token);
    const userInfo = decodeUserInfo(claims as any);

    const newSessionCookie = await encryptSession({
      user: {
        id: userInfo.id,
        tenantId: userInfo.tenantId,
        email: userInfo.email,
        name: userInfo.name,
        authorities: userInfo.authorities as any,
        productScope: userInfo.productScope as any,
      },
      accessToken: tokens.access_token,
      refreshToken: tokens.refresh_token, // rotated — old one is now invalid server-side
      accessTokenExpiresAt: Math.floor(Date.now() / 1000) + tokens.expires_in,
    });

    const response = NextResponse.json({ ok: true });
    response.cookies.set(
      SESSION_COOKIE_NAME,
      newSessionCookie,
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
    return response;
  }
}
