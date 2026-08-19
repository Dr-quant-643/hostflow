import { describe, it, expect } from "vitest";
import { CONSOLE_NAV } from "./nav-config";

describe("CONSOLE_NAV", () => {
  it("covers Organizations/Platform Users/System Health/Feature Flags/Audit Log/Monitoring", () => {
    expect(CONSOLE_NAV.map((i) => i.label)).toEqual([
      "Organizations",
      "Platform Users",
      "System Health",
      "Feature Flags",
      "Audit Log",
      "Monitoring",
    ]);
  });

  it("is platform-scoped, not product-scoped like hostflow-admin/nazilco-admin", () => {
    const paths = CONSOLE_NAV.map((i) => i.href);
    expect(paths).toContain("/organizations");
    expect(paths).not.toContain("/support");
    expect(paths).not.toContain("/billing");
  });
});
