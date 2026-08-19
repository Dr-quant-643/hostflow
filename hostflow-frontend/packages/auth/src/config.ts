// All Keycloak/BFF config in one place, env-driven. Matches the values
// documented in Part 2 of the backend handoff (issuer http://localhost:8081/
// realms/hostflow, client secrets per-environment).

function required(name: string, fallback?: string): string {
  const value = typeof process !== "undefined" ? process.env[name] : undefined;
  if (value) return value;
  if (fallback !== undefined) return fallback;
  throw new Error(`Missing required env var: ${name}`);
}

export function getAuthConfig() {
  return {
    issuer: required(
      "KEYCLOAK_ISSUER",
      "http://localhost:8081/realms/hostflow",
    ),
    clientId: required("KEYCLOAK_CLIENT_ID", "hostflow-web"),
    clientSecret: process.env.KEYCLOAK_CLIENT_SECRET, // absent for public/PKCE-only clients
    redirectUri: required(
      "KEYCLOAK_REDIRECT_URI",
      "http://localhost:3000/api/auth/callback",
    ),
    postLogoutRedirectUri: required(
      "KEYCLOAK_POST_LOGOUT_REDIRECT_URI",
      "http://localhost:3000",
    ),
    sessionSecret: required(
      "SESSION_SECRET",
      "dev-only-secret-change-in-production-min-32-chars!!",
    ),
    scope: "openid profile email",
  };
}
