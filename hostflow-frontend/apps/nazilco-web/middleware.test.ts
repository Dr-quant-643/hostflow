import { describe, it, expect } from "vitest";
import { PROTECTED_PATHS } from "./middleware";

// Since NextRequest is awkward to fully mock, this test verifies the
// protected-paths contract directly against the real exported constant
// (rather than a hardcoded copy that could drift silently) — guards against
// someone loosening PROTECTED_PATHS by accident (e.g. forgetting the
// leading slash, breaking startsWith matching).
describe("nazilco-web protected paths contract", () => {
  it("includes every guest-account section", () => {
    expect(PROTECTED_PATHS).toEqual([
      "/checkout",
      "/guest-portal",
      "/profile",
      "/invoices",
      "/notifications",
      "/support",
    ]);
  });

  it("does NOT include discovery/search/signup — those must stay public", () => {
    expect(PROTECTED_PATHS).not.toContain("/discover");
    expect(PROTECTED_PATHS).not.toContain("/search");
    expect(PROTECTED_PATHS).not.toContain("/signup");
  });

  it("every path starts with a leading slash for correct startsWith matching", () => {
    PROTECTED_PATHS.forEach((p) => expect(p.startsWith("/")).toBe(true));
  });
});
