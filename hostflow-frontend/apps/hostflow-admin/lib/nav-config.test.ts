import { describe, it, expect } from "vitest";
import { ADMIN_NAV } from "./nav-config";

describe("ADMIN_NAV", () => {
  it("covers exactly the support/billing/product/access scope", () => {
    expect(ADMIN_NAV.map((i) => i.label)).toEqual([
      "Support",
      "Billing",
      "Products",
      "Access Control",
    ]);
  });

  it("does not overlap with hostflow-web's owner-facing nav paths", () => {
    const adminPaths = ADMIN_NAV.map((i) => i.href);
    const ownerPaths = [
      "/dashboard",
      "/properties",
      "/bookings",
      "/crm",
      "/marketing",
      "/analytics",
      "/notifications",
      "/settings",
    ];
    adminPaths.forEach((p) => expect(ownerPaths).not.toContain(p));
  });
});
