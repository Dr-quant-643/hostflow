// Cookie name is per-product now — see session.ts's csrfCookieName(prefix).
export function generateCsrfToken(): string {
  const bytes = new Uint8Array(24);
  crypto.getRandomValues(bytes);
  return Buffer.from(bytes).toString("base64url");
}
