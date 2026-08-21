import { describe, it, expect, beforeEach } from "vitest";
import { encryptSession, decryptSession } from "./session";

describe("session encryption", () => {
  beforeEach(() => {
    vi.stubEnv?.("SESSION_SECRET", "test-secret-at-least-32-characters-long!!");
  });

  const sampleSession = {
    user: {
      id: "u1",
      tenantId: "t1",
      email: "owner@example.com",
      name: "Jane Owner",
      authorities: ["PRODUCT_XANUOS" as const],
      productScope: ["PRODUCT_XANUOS" as const],
    },
    accessToken: "fake.access.token",
    refreshToken: "fake.refresh.token",
    accessTokenExpiresAt: Math.floor(Date.now() / 1000) + 900,
  };

  it("round-trips a session through encrypt/decrypt", async () => {
    const cookie = await encryptSession(sampleSession);
    const decrypted = await decryptSession(cookie);
    expect(decrypted?.user.email).toBe("owner@example.com");
    expect(decrypted?.accessToken).toBe("fake.access.token");
  });

  it("returns null for a tampered cookie", async () => {
    const cookie = await encryptSession(sampleSession);
    const tampered = cookie.slice(0, -5) + "AAAAA";
    const decrypted = await decryptSession(tampered);
    expect(decrypted).toBeNull();
  });

  it("returns null for garbage input", async () => {
    const decrypted = await decryptSession("not-a-real-jwt");
    expect(decrypted).toBeNull();
  });
});
