import { SignJWT, jwtVerify } from "jose";
import type { SessionUser } from "@hostflow/types";
import { getAuthConfig } from "./config";

// The BROWSER-FACING session cookie. Encrypted/signed with SESSION_SECRET,
// contains SessionUser (safe, minimal) PLUS the real Keycloak access/refresh
// tokens so the BFF can use them server-side — but this cookie is httpOnly,
// so client JS never reads the tokens even though they're present.
//
// Deliberately kept as ONE cookie rather than split: this Vercel deployment
// silently corrupts any response that sets 2+ cookies at once (multiple
// Set-Cookie headers arrive comma-joined into one malformed header, and
// every client — curl, every real browser — then only parses the first
// cookie in the joined string, silently dropping the rest). Routes that
// genuinely need more than one cookie set at once (callback, logout) work
// around this by chaining an extra redirect hop instead, one cookie set per
// response — see routes/callback.ts and routes/finish.ts.
interface ServerSession {
  user: SessionUser;
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: number; // epoch seconds
}

function getSecretKey(): Uint8Array {
  return new TextEncoder().encode(getAuthConfig().sessionSecret);
}

export async function encryptSession(session: ServerSession): Promise<string> {
  return new SignJWT({ ...session })
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime("30d") // matches backend refresh token lifetime
    .sign(getSecretKey());
}

export async function decryptSession(
  cookieValue: string,
): Promise<ServerSession | null> {
  try {
    const { payload } = await jwtVerify(cookieValue, getSecretKey());
    return payload as unknown as ServerSession;
  } catch {
    return null;
  }
}

export const SESSION_COOKIE_NAME = "hostflow_session";

export const sessionCookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  path: "/",
};

export type { ServerSession };
