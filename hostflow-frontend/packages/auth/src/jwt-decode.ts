// Lightweight, non-verifying decode — used ONLY to read claims out of a
// token we already validated by virtue of having just received it directly
// from Keycloak's token endpoint over TLS (server-to-server). Never use
// this to "verify" a token that arrived from an untrusted source.

export function decodeJwtPayload<T = Record<string, unknown>>(
  token: string,
): T {
  const parts = token.split(".");
  if (parts.length !== 3 || parts[1] === undefined) throw new Error("Malformed JWT");
  const payload = Buffer.from(parts[1], "base64url").toString("utf-8");
  return JSON.parse(payload) as T;
}
