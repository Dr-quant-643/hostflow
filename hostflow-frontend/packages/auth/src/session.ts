import { SignJWT, jwtVerify } from "jose";
import type { SessionUser } from "@hostflow/types";
import { getAuthConfig } from "./config";

// Split across two cookies rather than one: a signed Keycloak access +
// refresh token pair, once re-signed into a wrapper JWT alongside the user
// object, routinely landed at 3.3-3.5KB and occasionally crossed the ~4096
// byte per-cookie limit browsers enforce -- which they enforce by silently
// dropping the Set-Cookie instead of erroring. The callback route would
// "succeed" and redirect home, but the cookie was never actually stored, so
// the very next request looked unauthenticated again -- an infinite
// login -> callback -> login redirect loop with no visible error.
// UserSession (small, identity + authorities only) is what middleware needs
// on every request; TokenSession (the actual Keycloak tokens) is only ever
// read from Node route handlers/server components, never from Edge
// middleware, so keeping it in a separate cookie costs nothing.
interface UserSession {
  user: SessionUser;
}

interface TokenSession {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: number; // epoch seconds
}

function getSecretKey(): Uint8Array {
  return new TextEncoder().encode(getAuthConfig().sessionSecret);
}

async function sign(payload: Record<string, unknown>): Promise<string> {
  return new SignJWT({ ...payload })
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime("30d") // matches backend refresh token lifetime
    .sign(getSecretKey());
}

async function verify<T>(cookieValue: string): Promise<T | null> {
  try {
    const { payload } = await jwtVerify(cookieValue, getSecretKey());
    return payload as unknown as T;
  } catch {
    return null;
  }
}

export async function encryptUserSession(session: UserSession): Promise<string> {
  return sign({ ...session });
}

export async function decryptUserSession(
  cookieValue: string,
): Promise<UserSession | null> {
  return verify<UserSession>(cookieValue);
}

export async function encryptTokenSession(
  session: TokenSession,
): Promise<string> {
  return sign({ ...session });
}

export async function decryptTokenSession(
  cookieValue: string,
): Promise<TokenSession | null> {
  return verify<TokenSession>(cookieValue);
}

export const SESSION_COOKIE_NAME = "hostflow_session";
export const TOKEN_COOKIE_NAME = "hostflow_tokens";

export const sessionCookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  path: "/",
};

export type { UserSession, TokenSession };
