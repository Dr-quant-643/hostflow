import { describe, it, expect, beforeEach } from "vitest";
import {
  encryptUserSession,
  decryptUserSession,
  encryptTokenSession,
  decryptTokenSession,
} from "./session";

describe("session encryption", () => {
  beforeEach(() => {
    vi.stubEnv?.("SESSION_SECRET", "test-secret-at-least-32-characters-long!!");
  });

  const sampleUserSession = {
    user: {
      id: "u1",
      tenantId: "t1",
      email: "owner@example.com",
      name: "Jane Owner",
      authorities: ["PRODUCT_XANUOS" as const],
      productScope: ["PRODUCT_XANUOS" as const],
    },
  };

  const sampleTokenSession = {
    accessToken: "fake.access.token",
    refreshToken: "fake.refresh.token",
    accessTokenExpiresAt: Math.floor(Date.now() / 1000) + 900,
  };

  it("round-trips a user session through encrypt/decrypt", async () => {
    const cookie = await encryptUserSession(sampleUserSession);
    const decrypted = await decryptUserSession(cookie);
    expect(decrypted?.user.email).toBe("owner@example.com");
  });

  it("round-trips a token session through encrypt/decrypt", async () => {
    const cookie = await encryptTokenSession(sampleTokenSession);
    const decrypted = await decryptTokenSession(cookie);
    expect(decrypted?.accessToken).toBe("fake.access.token");
  });

  it("returns null for a tampered user cookie", async () => {
    const cookie = await encryptUserSession(sampleUserSession);
    const tampered = cookie.slice(0, -5) + "AAAAA";
    const decrypted = await decryptUserSession(tampered);
    expect(decrypted).toBeNull();
  });

  it("returns null for garbage input", async () => {
    const decrypted = await decryptUserSession("not-a-real-jwt");
    expect(decrypted).toBeNull();
  });
});
