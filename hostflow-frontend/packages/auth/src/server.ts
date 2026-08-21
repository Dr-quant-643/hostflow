import { cookies } from "next/headers";
import {
  decryptUserSession,
  decryptTokenSession,
  SESSION_COOKIE_NAME,
  TOKEN_COOKIE_NAME,
} from "./session";
import type { Authority, ProductScope, SessionUser } from "@hostflow/types";

// Server Component / Route Handler helper: resolves the current session
// without ever exposing the access token to caller code that doesn't need
// it. Use getServerSession() in layouts/pages for the user object; use
// getValidAccessToken() only from server-side data-fetching code that talks
// to the Gateway directly.

/**
 * TEMPORARY dev-only preview aid — never enabled unless DEV_MOCK_AUTH=true is
 * explicitly set in an app's .env.local. Lets every page render with a fake
 * session so the UI can be reviewed without a running Keycloak. Each app
 * configures its own mock identity (role/product scope) via env vars so the
 * preview matches who would actually use that app. Remove/disable before any
 * real auth testing.
 */
function getMockSessionUser(): SessionUser {
  const authorities = (process.env.DEV_MOCK_AUTHORITIES ?? "PRODUCT_XANUOS")
    .split(",")
    .map((a) => a.trim()) as Authority[];
  const productScope = (process.env.DEV_MOCK_PRODUCT_SCOPE ?? "PRODUCT_XANUOS")
    .split(",")
    .map((s) => s.trim()) as ProductScope[];

  return {
    id: process.env.DEV_MOCK_USER_ID ?? "00000000-0000-0000-0000-000000000001",
    tenantId: process.env.DEV_MOCK_TENANT_ID ?? "00000000-0000-0000-0000-0000000000aa",
    email: process.env.DEV_MOCK_EMAIL ?? "dev@hostflow.local",
    name: process.env.DEV_MOCK_NAME ?? "Dev Preview User",
    authorities,
    productScope,
  };
}

export async function getServerSession(): Promise<SessionUser | null> {
  if (process.env.DEV_MOCK_AUTH === "true") {
    return getMockSessionUser();
  }

  const cookieStore = cookies();
  const raw = cookieStore.get(SESSION_COOKIE_NAME)?.value;
  if (!raw) return null;
  const session = await decryptUserSession(raw);
  return session?.user ?? null;
}

/**
 * Returns a valid access token, transparently refreshing if the current one
 * is within 30s of expiry. Callers (server-side route handlers proxying to
 * the Gateway) use this rather than reading the session cookie directly.
 */
export async function getValidAccessToken(): Promise<string | null> {
  if (process.env.DEV_MOCK_AUTH === "true") {
    return "dev-mock-access-token";
  }

  const cookieStore = cookies();
  const raw = cookieStore.get(TOKEN_COOKIE_NAME)?.value;
  if (!raw) return null;
  const session = await decryptTokenSession(raw);
  if (!session) return null;

  const now = Math.floor(Date.now() / 1000);
  if (session.accessTokenExpiresAt - now > 30) {
    return session.accessToken;
  }
  // Expired/near-expiry — caller's route should hit /api/auth/refresh first.
  // Kept simple here; refresh orchestration lives in routes/refresh.ts.
  return null;
}

export function hasAuthority(
  user: SessionUser | null,
  authority: string,
): boolean {
  return !!user?.authorities?.includes(authority as any);
}

export function hasProductScope(
  user: SessionUser | null,
  scope: "PRODUCT_XANUOS" | "PRODUCT_NAZILCO",
): boolean {
  return !!user?.productScope?.includes(scope);
}
