import { describe, it, expect } from "vitest";
import { XANUOS_NAV } from "./xanuos-nav-config";

describe("XANUOS_NAV", () => {
  it("has 9 entries matching the locked nav order", () => {
    expect(XANUOS_NAV).toHaveLength(9);
    expect(XANUOS_NAV.map((i) => i.label)).toEqual([
      "Dashboard",
      "Properties",
      "Bookings",
      "CRM",
      "Marketing",
      "Billing",
      "Analytics",
      "Notifications",
      "Settings",
    ]);
  });

  it("every item has a unique, absolute href", () => {
    const hrefs = XANUOS_NAV.map((i) => i.href);
    expect(new Set(hrefs).size).toBe(hrefs.length);
    hrefs.forEach((h) => expect(h.startsWith("/")).toBe(true));
  });

  it("every item has an icon component assigned", () => {
    XANUOS_NAV.forEach((item) => expect(item.icon).toBeDefined());
  });
});
