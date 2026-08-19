import { describe, it, expect } from "vitest";
import { sapphire, purple, neutral, success, warning, error } from "./colors";
import { semanticLight, semanticDark } from "./semantic";

const REQUIRED_SHADE_KEYS = ["50", "500", "900"];

function hexToLuminance(hex: string): number {
  const c = hex.replace("#", "");
  const r = parseInt(c.substring(0, 2), 16) / 255;
  const g = parseInt(c.substring(2, 4), 16) / 255;
  const b = parseInt(c.substring(4, 6), 16) / 255;
  const channels = [r, g, b].map((v) =>
    v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4),
  );
  const rl = channels[0] ?? 0;
  const gl = channels[1] ?? 0;
  const bl = channels[2] ?? 0;
  return 0.2126 * rl + 0.7152 * gl + 0.0722 * bl;
}

function contrastRatio(hex1: string, hex2: string): number {
  const l1 = hexToLuminance(hex1) + 0.05;
  const l2 = hexToLuminance(hex2) + 0.05;
  return l1 > l2 ? l1 / l2 : l2 / l1;
}

describe("color scales", () => {
  it("sapphire has required shade keys", () => {
    REQUIRED_SHADE_KEYS.forEach((k) => expect(sapphire).toHaveProperty(k));
  });
  it("purple has required shade keys", () => {
    REQUIRED_SHADE_KEYS.forEach((k) => expect(purple).toHaveProperty(k));
  });
  it("neutral has required shade keys plus 950", () => {
    [...REQUIRED_SHADE_KEYS, "950"].forEach((k) =>
      expect(neutral).toHaveProperty(k),
    );
  });
  it("semantic status colors are defined", () => {
    expect(success[500]).toBeDefined();
    expect(warning[500]).toBeDefined();
    expect(error[500]).toBeDefined();
  });
});

describe("WCAG AA contrast (4.5:1 minimum for body text)", () => {
  it("light mode: foreground on background passes AA", () => {
    const ratio = contrastRatio(
      semanticLight.foreground,
      semanticLight.background,
    );
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
  it("light mode: mutedForeground on background passes AA", () => {
    const ratio = contrastRatio(
      semanticLight.mutedForeground,
      semanticLight.background,
    );
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
  it("light mode: primaryForeground on primary passes AA", () => {
    const ratio = contrastRatio(
      semanticLight.primaryForeground,
      semanticLight.primary,
    );
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
  it("dark mode: foreground on background passes AA", () => {
    const ratio = contrastRatio(
      semanticDark.foreground,
      semanticDark.background,
    );
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
  it("dark mode: mutedForeground on background passes AA", () => {
    const ratio = contrastRatio(
      semanticDark.mutedForeground,
      semanticDark.background,
    );
    expect(ratio).toBeGreaterThanOrEqual(4.5);
  });
});
