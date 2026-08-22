// All Keycloak/BFF config in one place, env-driven, per product/workspace.
//
// A merged app (hostflow-web hosting both XanuOS and NazilCo, or
// hostflow-admin hosting both admin workspaces) needs two fully independent
// identities side by side — a XanuOS account and a NazilCo account are
// never the same login, and neither session should ever be visible while
// rendering the other product's routes. createAuthConfig(prefix, basePath)
// is called once per product with a prefix like "XANUOS" or "NAZILCO", and
// reads ${prefix}_KEYCLOAK_ISSUER etc. instead of a single fixed set of env
// vars.
//
// xanuos-console is untouched by the 5-to-3 app migration and stays a
// single standalone identity — calling createAuthConfig("", "") (empty
// prefix, empty basePath) reads the exact unprefixed env var names
// (KEYCLOAK_ISSUER, KEYCLOAK_CLIENT_ID, SESSION_SECRET, ...) it has always
// used, and every function throughout this package that takes a config
// defaults to exactly this so xanuos-console's call sites never had to
// change.

function required(name: string, fallback?: string): string {
  const value = typeof process !== "undefined" ? process.env[name] : undefined;
  if (value) return value;
  if (fallback !== undefined) return fallback;
  throw new Error(`Missing required env var: ${name}`);
}

export interface AuthConfig {
  prefix: string;
  /** URL path segment this product is mounted under, e.g. "/xanuos" (empty
   *  string for a standalone app like xanuos-console) — every redirect the
   *  auth routes issue (to /api/auth/finish, back to home, etc.) is built
   *  relative to this. */
  basePath: string;
  issuer: string;
  clientId: string;
  clientSecret: string | undefined; // absent for public/PKCE-only clients
  redirectUri: string;
  postLogoutRedirectUri: string;
  sessionSecret: string;
  sessionCookieName: string;
  scope: string;
}

export function createAuthConfig(prefix = "", basePath = ""): AuthConfig {
  const p = prefix.toUpperCase();
  const envPrefix = p ? `${p}_` : "";
  const appBaseUrl = required("APP_BASE_URL", "http://localhost:3000");
  return {
    prefix: p,
    basePath,
    issuer: required(
      `${envPrefix}KEYCLOAK_ISSUER`,
      "http://localhost:8081/realms/hostflow",
    ),
    clientId: required(`${envPrefix}KEYCLOAK_CLIENT_ID`),
    clientSecret: process.env[`${envPrefix}KEYCLOAK_CLIENT_SECRET`],
    // For a prefixed (merged-app) product, derive the redirect URI from
    // APP_BASE_URL + basePath rather than a separately-configured env var —
    // this deployment's very first login attempts all failed with
    // Keycloak's "Invalid parameter: redirect_uri" because every product's
    // redirect URI had to be set manually and one was missed; deriving it
    // removes that entire class of misconfiguration. xanuos-console (no
    // prefix) keeps reading its existing explicit env vars unchanged.
    redirectUri: p
      ? `${appBaseUrl}${basePath}/api/auth/callback`
      : required("KEYCLOAK_REDIRECT_URI"),
    // The site-wide homepage, not this product's own basePath — logging out
    // of one product should land you back at the neutral "choose a product"
    // picker, not the same product's marketing page (which looks nearly
    // identical logged-in vs logged-out, so it can read as "nothing
    // happened"). Requires this exact URL to be registered on the Keycloak
    // client's post-logout-redirect-uris, not just its regular redirect URIs.
    postLogoutRedirectUri: p
      ? appBaseUrl
      : required("KEYCLOAK_POST_LOGOUT_REDIRECT_URI"),
    sessionSecret: required(
      `${envPrefix}SESSION_SECRET`,
      "dev-only-secret-change-in-production-min-32-chars!!",
    ),
    sessionCookieName: required(
      `${envPrefix}SESSION_COOKIE_NAME`,
      p ? `hostflow_session_${p.toLowerCase()}` : "hostflow_session",
    ),
    scope: "openid profile email",
  };
}
