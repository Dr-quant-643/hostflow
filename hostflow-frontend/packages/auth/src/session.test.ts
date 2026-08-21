import { describe, it, expect } from "vitest";
import { encryptSession, decryptSession } from "./session";

describe("session encryption", () => {
  const secret = "test-secret-at-least-32-characters-long!!";

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
    const cookie = await encryptSession(sampleSession, secret);
    const decrypted = await decryptSession(cookie, secret);
    expect(decrypted?.user.email).toBe("owner@example.com");
    expect(decrypted?.accessToken).toBe("fake.access.token");
  });

  it("returns null when decrypted with the wrong secret", async () => {
    const cookie = await encryptSession(sampleSession, secret);
    const decrypted = await decryptSession(cookie, "a-completely-different-secret-32-chars!!");
    expect(decrypted).toBeNull();
  });

  it("returns null for a tampered cookie", async () => {
    const cookie = await encryptSession(sampleSession, secret);
    const tampered = cookie.slice(0, -5) + "AAAAA";
    const decrypted = await decryptSession(tampered, secret);
    expect(decrypted).toBeNull();
  });

  it("returns null for garbage input", async () => {
    const decrypted = await decryptSession("not-a-real-jwt", secret);
    expect(decrypted).toBeNull();
  });
});
