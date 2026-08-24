import { NextRequest, NextResponse } from "next/server";
import { exchangeCodeForTokens, decodeUserInfo } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import { encryptSession, sessionCookieOptions, oauthFlowCookieName } from "../session";
import { createAuthConfig, type AuthConfig } from "../config";
import { base64UrlDecodeString } from "../pkce";

// Mount per product at e.g. app/xanuos/api/auth/callback/route.ts as:
//   export const GET = createCallbackRoute(createAuthConfig("XANUOS", "/xanuos"));
export function createCallbackRoute(config: AuthConfig) {
  return async function GET(request: NextRequest) {
    const url = new URL(request.url);
    const code = url.searchParams.get("code");
    const state = url.searchParams.get("state");
    const flowCookie = request.cookies.get(oauthFlowCookieName(config.prefix))?.value;
    const [expectedState, verifier, encodedReturnTo] = flowCookie?.split(".") ?? [];

    if (!code || !state || !verifier || state !== expectedState) {
      return NextResponse.redirect(
        new URL(`${config.basePath}?authError=invalid_state`, request.url),
      );
    }

    try {
      const tokens = await exchangeCodeForTokens(config, code, verifier);
      const claims = decodeJwtPayload(tokens.access_token);
      const userInfo = decodeUserInfo(claims as any);

      const sessionCookie = await encryptSession(
        {
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
        },
        config.sessionSecret,
      );

      // Redirects to ${basePath}/api/auth/finish rather than setting the
      // CSRF cookie here directly -- this deployment corrupts any response
      // that sets 2+ cookies at once (see session.ts), and this response
      // already needs to both set the session cookie AND (implicitly)
      // leave oauth_flow to expire on its own. One more redirect hop keeps
      // every response down to a single Set-Cookie.
      const finishUrl = new URL(`${config.basePath}/api/auth/finish`, request.url);
      if (encodedReturnTo) {
        const returnTo = base64UrlDecodeString(encodedReturnTo);
        // Defense in depth against an open redirect: returnTo only ever
        // originates from our own middleware (always a same-product path),
        // but re-validate here since it round-tripped through a cookie.
        if (returnTo.startsWith(config.basePath)) {
          finishUrl.searchParams.set("returnTo", returnTo);
        }
      }
      const response = NextResponse.redirect(finishUrl);
      response.cookies.set(config.sessionCookieName, sessionCookie, sessionCookieOptions);
      return response;
    } catch {
      return NextResponse.redirect(
        new URL(`${config.basePath}?authError=token_exchange_failed`, request.url),
      );
    }
  };
}

// Backward-compat bare export — see login.ts for why this is lazy.
export const GET = (request: NextRequest) =>
  createCallbackRoute(createAuthConfig())(request);
