import type { AuthConfig } from "./config";

interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  token_type: string;
}

interface KeycloakUserInfo {
  sub: string;
  email?: string;
  name?: string;
  tenant_id?: string;
  product_scope?: string[];
  realm_access?: { roles?: string[] };
}

function tokenEndpoint(config: AuthConfig): string {
  return `${config.issuer}/protocol/openid-connect/token`;
}

export async function exchangeCodeForTokens(
  config: AuthConfig,
  code: string,
  codeVerifier: string,
): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: "authorization_code",
    client_id: config.clientId,
    code,
    redirect_uri: config.redirectUri,
    code_verifier: codeVerifier,
  });
  if (config.clientSecret) body.set("client_secret", config.clientSecret);

  const response = await fetch(tokenEndpoint(config), {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body,
  });
  if (!response.ok) {
    throw new Error(`Keycloak token exchange failed: ${response.status}`);
  }
  return response.json();
}

export async function refreshTokens(
  config: AuthConfig,
  refreshToken: string,
): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: "refresh_token",
    client_id: config.clientId,
    refresh_token: refreshToken,
  });
  if (config.clientSecret) body.set("client_secret", config.clientSecret);

  const response = await fetch(tokenEndpoint(config), {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body,
  });
  if (!response.ok) {
    // Reuse detection: a failed refresh (e.g. token already rotated/revoked)
    // must force a full re-login, never a silent retry.
    throw new Error(`Keycloak token refresh failed: ${response.status}`);
  }
  return response.json();
}

// Keycloak's access token never actually carries an "authorities" claim —
// that's a Spring-side concept. The backend's HostFlowJwtAuthenticationConverter
// derives it per-request from realm_access.roles ("ROLE_<UPPERCASED_ROLE>")
// and product_scope ("PRODUCT_<SCOPE>"); this mirrors that exactly so the
// frontend's authorityGate middleware sees the same authorities the backend
// would compute, instead of always reading an empty array and redirecting
// every real login to /access-denied.
export function decodeUserInfo(accessTokenPayload: KeycloakUserInfo): {
  id: string;
  email?: string;
  name?: string;
  tenantId: string;
  productScope: string[];
  authorities: string[];
} {
  const roleAuthorities = (accessTokenPayload.realm_access?.roles ?? []).map(
    (role) => `ROLE_${role.toUpperCase()}`,
  );
  const productScope = accessTokenPayload.product_scope ?? [];
  const productAuthorities = productScope.map((scope) => `PRODUCT_${scope.toUpperCase()}`);

  return {
    id: accessTokenPayload.sub,
    email: accessTokenPayload.email,
    name: accessTokenPayload.name,
    tenantId: accessTokenPayload.tenant_id ?? "",
    productScope,
    authorities: [...roleAuthorities, ...productAuthorities],
  };
}

export function buildAuthorizationUrl(
  config: AuthConfig,
  params: {
    state: string;
    codeChallenge: string;
  },
): string {
  const url = new URL(`${config.issuer}/protocol/openid-connect/auth`);
  url.searchParams.set("client_id", config.clientId);
  url.searchParams.set("redirect_uri", config.redirectUri);
  url.searchParams.set("response_type", "code");
  url.searchParams.set("scope", config.scope);
  url.searchParams.set("state", params.state);
  url.searchParams.set("code_challenge", params.codeChallenge);
  url.searchParams.set("code_challenge_method", "S256");
  return url.toString();
}

export function buildLogoutUrl(config: AuthConfig): string {
  const url = new URL(`${config.issuer}/protocol/openid-connect/logout`);
  url.searchParams.set("client_id", config.clientId);
  url.searchParams.set(
    "post_logout_redirect_uri",
    config.postLogoutRedirectUri,
  );
  return url.toString();
}
