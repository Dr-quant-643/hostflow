// RFC 7636 PKCE — code_verifier/code_challenge generation. Runs server-side
// only (Node's webcrypto), invoked from the /api/auth/login route handler.

function base64UrlEncode(buffer: ArrayBuffer): string {
  return Buffer.from(buffer)
    .toString("base64")
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");
}

export function generateCodeVerifier(): string {
  const bytes = new Uint8Array(32);
  crypto.getRandomValues(bytes);
  return base64UrlEncode(bytes.buffer);
}

export async function generateCodeChallenge(verifier: string): Promise<string> {
  const encoder = new TextEncoder();
  const data = encoder.encode(verifier);
  const digest = await crypto.subtle.digest("SHA-256", data);
  return base64UrlEncode(digest);
}

export function generateState(): string {
  const bytes = new Uint8Array(16);
  crypto.getRandomValues(bytes);
  return base64UrlEncode(bytes.buffer);
}

// For packing an arbitrary returnTo path into the oauth_flow cookie alongside
// state/verifier (see login.ts/callback.ts) without colliding with the "."
// delimiter those two already use -- base64url's alphabet never contains ".".
export function base64UrlEncodeString(value: string): string {
  return base64UrlEncode(new TextEncoder().encode(value).buffer as ArrayBuffer);
}

export function base64UrlDecodeString(value: string): string {
  const padded = value.replace(/-/g, "+").replace(/_/g, "/").padEnd(Math.ceil(value.length / 4) * 4, "=");
  return Buffer.from(padded, "base64").toString("utf-8");
}
