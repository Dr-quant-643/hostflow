import { NextRequest, NextResponse } from "next/server";
import { refreshTokens, decodeUserInfo } from "../keycloak-client";
import { decodeJwtPayload } from "../jwt-decode";
import { decryptSession, encryptSession, sessionCookieOptions } from "../session";
import { type AuthConfig } from "../config";

// The missing link in the BFF pattern described throughout this package:
// getValidAccessToken() existed to let a server-side route attach a Bearer
// token when proxying to the Gateway, but no route ever actually called it —
// every client-side mutation (property creation, bookings, etc.) was calling
// the Gateway directly with only cookies attached, via
// NEXT_PUBLIC_API_BASE_URL pointed at the Gateway's public URL. The Gateway
// only understands JWT Bearer tokens, not this app's session cookie, so
// every one of those calls was a guaranteed 401 in a real browser.
//
// Mount per product at e.g. app/xanuos/api/v1/[...path]/route.ts as:
//   const handler = createGatewayProxyRoute(createAuthConfig("XANUOS", "/xanuos"), GATEWAY_BASE_URL);
//   export { handler as GET, handler as POST, handler as PATCH, handler as PUT, handler as DELETE };
export function createGatewayProxyRoute(config: AuthConfig, gatewayBaseUrl: string) {
  return async function handler(
    request: NextRequest,
    { params }: { params: { path?: string[] } },
  ) {
    // No session (or an invalid one) is not necessarily an error here — NazilCo
    // has genuinely anonymous browsing endpoints (public property listings,
    // mall directory). Forward without a Bearer token and let the Gateway's
    // own security config decide: permitAll paths work anonymously, anything
    // else correctly 401s from the Gateway itself rather than the proxy.
    const raw = request.cookies.get(config.sessionCookieName)?.value;
    let session = raw ? await decryptSession(raw, config.sessionSecret) : null;

    let setCookieValue: string | null = null;
    const now = Math.floor(Date.now() / 1000);
    if (session && session.accessTokenExpiresAt - now <= 30) {
      // Access token expired or about to — refresh inline rather than
      // requiring every client call site to orchestrate a separate
      // /api/auth/refresh round trip first.
      try {
        const tokens = await refreshTokens(config, session.refreshToken);
        const claims = decodeJwtPayload(tokens.access_token);
        const userInfo = decodeUserInfo(claims as any);
        session = {
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
        };
        setCookieValue = await encryptSession(session, config.sessionSecret);
      } catch {
        const response = NextResponse.json(
          { success: false, error: { code: "HF-401", message: "Session expired" } },
          { status: 401 },
        );
        response.cookies.delete(config.sessionCookieName);
        return response;
      }
    }

    const path = (params.path ?? []).join("/");
    const targetUrl = `${gatewayBaseUrl}/api/v1/${path}${request.nextUrl.search}`;

    const forwardHeaders = new Headers();
    const contentType = request.headers.get("content-type");
    if (contentType) forwardHeaders.set("content-type", contentType);
    if (session) forwardHeaders.set("authorization", `Bearer ${session.accessToken}`);
    forwardHeaders.set("accept", "application/json");

    const hasBody = !["GET", "HEAD", "DELETE"].includes(request.method);

    let gatewayResponse: Response;
    try {
      gatewayResponse = await fetch(targetUrl, {
        method: request.method,
        headers: forwardHeaders,
        body: hasBody ? await request.arrayBuffer() : undefined,
      });
    } catch {
      return NextResponse.json(
        { success: false, error: { code: "HF-502", message: "Could not reach the backend" } },
        { status: 502 },
      );
    }

    const responseBody = await gatewayResponse.arrayBuffer();
    const response = new NextResponse(responseBody, {
      status: gatewayResponse.status,
      headers: {
        "content-type": gatewayResponse.headers.get("content-type") ?? "application/json",
      },
    });

    if (setCookieValue) {
      response.cookies.set(config.sessionCookieName, setCookieValue, sessionCookieOptions);
    }

    return response;
  };
}
