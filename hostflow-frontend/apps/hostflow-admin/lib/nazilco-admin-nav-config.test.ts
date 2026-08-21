import { describe, it, expect } from "vitest";
import { NAZILCO_ADMIN_NAV } from "./nazilco-admin-nav-config";

describe("NAZILCO_ADMIN_NAV", () => {
  it("covers exactly Support and Bookings Oversight — narrower than hostflow-admin's nav", () => {
    expect(NAZILCO_ADMIN_NAV.map((i) => i.label)).toEqual([
      "Support",
      "Bookings Oversight",
    ]);
  });

  it("has no Billing/Products/Access Control entries (out of scope for this app)", () => {
    const labels = NAZILCO_ADMIN_NAV.map((i) => i.label);
    expect(labels).not.toContain("Billing");
    expect(labels).not.toContain("Products");
    expect(labels).not.toContain("Access Control");
  });
});
