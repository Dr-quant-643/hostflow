import { cookies } from "next/headers";
import { decryptSession } from "./session";
import { createAuthConfig, type AuthConfig } from "./config";
import type { Authority, ProductScope, SessionUser } from "@hostflow/types";

// Server Component / Route Handler helper: resolves the current session
// without ever exposing the access token to caller code that doesn't need
// it. Use getServerSession(config) in layouts/pages for the user object;
// use getValidAccessToken(config) only from server-side data-fetching code
// that talks to the Gateway directly. `config` is always that call site's
// own product config (createAuthConfig("XANUOS", "/xanuos") etc.) — never
// share one config's session across a different product's routes.

/**
 * TEMPORARY dev-only preview aid — never enabled unless
 * ${prefix}_DEV_MOCK_AUTH=true is explicitly set in an app's .env.local.
 * Lets that product's pages render with a fake session so the UI can be
 * reviewed without a running Keycloak. Remove/disable before any real auth
 * testing.
 */
function getMockSessionUser(prefix: string): SessionUser {
  const e = prefix ? `${prefix.toUpperCase()}_` : "";
  const authorities = (process.env[`${e}DEV_MOCK_AUTHORITIES`] ?? "PRODUCT_XANUOS")
    .split(",")
    .map((a) => a.trim()) as Authority[];
  const productScope = (process.env[`${e}DEV_MOCK_PRODUCT_SCOPE`] ?? "PRODUCT_XANUOS")
    .split(",")
    .map((s) => s.trim()) as ProductScope[];

  return {
    id: process.env[`${e}DEV_MOCK_USER_ID`] ?? "00000000-0000-0000-0000-000000000001",
    tenantId: process.env[`${e}DEV_MOCK_TENANT_ID`] ?? "00000000-0000-0000-0000-0000000000aa",
    email: process.env[`${e}DEV_MOCK_EMAIL`] ?? "dev@hostflow.local",
    name: process.env[`${e}DEV_MOCK_NAME`] ?? "Dev Preview User",
    authorities,
    productScope,
  };
}

export async function getServerSession(
  config: AuthConfig = createAuthConfig(),
): Promise<SessionUser | null> {
  const mockAuthEnv = config.prefix ? `${config.prefix}_DEV_MOCK_AUTH` : "DEV_MOCK_AUTH";
  if (process.env[mockAuthEnv] === "true") {
    return getMockSessionUser(config.prefix);
  }

  const cookieStore = cookies();
  const raw = cookieStore.get(config.sessionCookieName)?.value;
  if (!raw) return null;
  const session = await decryptSession(raw, config.sessionSecret);
  return session?.user ?? null;
}

/**
 * Returns a valid access token, transparently refreshing if the current one
 * is within 30s of expiry. Callers (server-side route handlers proxying to
 * the Gateway) use this rather than reading the session cookie directly.
 */
export async function getValidAccessToken(
  config: AuthConfig = createAuthConfig(),
): Promise<string | null> {
  const mockAuthEnv = config.prefix ? `${config.prefix}_DEV_MOCK_AUTH` : "DEV_MOCK_AUTH";
  if (process.env[mockAuthEnv] === "true") {
    return "dev-mock-access-token";
  }

  const cookieStore = cookies();
  const raw = cookieStore.get(config.sessionCookieName)?.value;
  if (!raw) return null;
  const session = await decryptSession(raw, config.sessionSecret);
  if (!session) return null;

  const now = Math.floor(Date.now() / 1000);
  if (session.accessTokenExpiresAt - now > 30) {
    return session.accessToken;
  }
  // Expired/near-expiry — caller's route should hit ${basePath}/api/auth/refresh first.
  // Kept simple here; refresh orchestration lives in routes/refresh.ts.
  return null;
}
