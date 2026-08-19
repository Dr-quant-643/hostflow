import { describe, it, expect } from "vitest";
import type { OrganizationRow } from "@hostflow/types";

// Type-shape check against the real PlatformOrganizationQueries.OrganizationRow
// projection — active is a boolean, not a status enum, and there is no
// SUSPENDED value anywhere in the backend (OrganizationController only
// exposes onboard + rename, no suspend/deactivate mutation).
describe("OrganizationRow shape", () => {
  it("uses a boolean active flag, not a status enum", () => {
    const sample: OrganizationRow = {
      id: "1",
      name: "Acme",
      slug: "acme",
      primaryProduct: "XANUOS",
      active: true,
      createdAt: new Date().toISOString(),
    };
    expect(sample.active).toBe(true);
  });
});
