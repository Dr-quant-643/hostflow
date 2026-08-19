import { describe, it, expect } from "vitest";

// Documents the assumption this phase rests on, as an executable spec
// rather than just a comment — if the real interaction-type enum is ever
// imported here from @hostflow/types, this test should be rewritten to
// check membership in that enum instead of a hardcoded string.
describe("Support interaction type convention", () => {
  it("assumes SUPPORT_REQUEST as the CRM interaction type filter", () => {
    const ASSUMED_SUPPORT_TYPE = "SUPPORT_REQUEST";
    expect(ASSUMED_SUPPORT_TYPE).toBe("SUPPORT_REQUEST");
  });
});
