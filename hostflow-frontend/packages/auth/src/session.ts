import { SignJWT, jwtVerify } from "jose";
import type { SessionUser } from "@hostflow/types";

// The BROWSER-FACING session cookie. Encrypted/signed with a per-product
// SESSION_SECRET (see config.ts's createAuthConfig), contains SessionUser
// (safe, minimal) PLUS the real Keycloak access/refresh tokens so the BFF
// can use them server-side — but this cookie is httpOnly, so client JS
// never reads the tokens even though they're present.
//
// Deliberately kept as ONE cookie rather than split across several: this
// Vercel deployment silently corrupts any response that sets 2+ cookies at
// once (multiple Set-Cookie headers arrive comma-joined into one malformed
// header, and every client — curl, every real browser — then only parses
// the first cookie in the joined string, silently dropping the rest).
// Routes that genuinely need more than one cookie set at once (callback,
// logout) work around this by chaining an extra redirect hop instead, one
// cookie set per response — see routes/callback.ts and routes/finish.ts.
interface ServerSession {
  user: SessionUser;
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: number; // epoch seconds
}

function secretKey(secret: string): Uint8Array {
  return new TextEncoder().encode(secret);
}

export async function encryptSession(
  session: ServerSession,
  secret: string,
): Promise<string> {
  return new SignJWT({ ...session })
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime("30d") // matches backend refresh token lifetime
    .sign(secretKey(secret));
}

export async function decryptSession(
  cookieValue: string,
  secret: string,
): Promise<ServerSession | null> {
  try {
    const { payload } = await jwtVerify(cookieValue, secretKey(secret));
    return payload as unknown as ServerSession;
  } catch {
    return null;
  }
}

export const sessionCookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  path: "/",
};

// oauth_flow and the CSRF cookie are also prefixed per product -- two login
// flows (one per product) can legitimately be in flight in the same
// browser at once, and the CSRF cookie is non-httpOnly (client JS reads it
// to echo back per the synchronizer pattern), so without prefixing, a
// XanuOS page could accidentally pick up NazilCo's CSRF token or vice versa.
export function oauthFlowCookieName(prefix = ""): string {
  return prefix ? `oauth_flow_${prefix.toLowerCase()}` : "oauth_flow";
}

export function csrfCookieName(prefix = ""): string {
  return prefix ? `XSRF-TOKEN-${prefix.toUpperCase()}` : "XSRF-TOKEN";
}

export type { ServerSession };
