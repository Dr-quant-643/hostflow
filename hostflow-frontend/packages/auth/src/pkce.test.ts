import { describe, it, expect } from "vitest";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
} from "./pkce";

describe("PKCE generation", () => {
  it("generates a code_verifier of sufficient length (RFC 7636: 43-128 chars)", () => {
    const verifier = generateCodeVerifier();
    expect(verifier.length).toBeGreaterThanOrEqual(43);
    expect(verifier.length).toBeLessThanOrEqual(128);
  });

  it("generates a URL-safe verifier (no +, /, =)", () => {
    const verifier = generateCodeVerifier();
    expect(verifier).not.toMatch(/[+/=]/);
  });

  it("produces a deterministic S256 challenge for a given verifier", async () => {
    const verifier = "test-verifier-fixed-value-for-deterministic-check";
    const challenge1 = await generateCodeChallenge(verifier);
    const challenge2 = await generateCodeChallenge(verifier);
    expect(challenge1).toBe(challenge2);
  });

  it("produces different challenges for different verifiers", async () => {
    const c1 = await generateCodeChallenge("verifier-one");
    const c2 = await generateCodeChallenge("verifier-two");
    expect(c1).not.toBe(c2);
  });

  it("generates a unique state per call", () => {
    const s1 = generateState();
    const s2 = generateState();
    expect(s1).not.toBe(s2);
  });
});
