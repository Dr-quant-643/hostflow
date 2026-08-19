import type { LucideIcon } from "lucide-react";
import { Building, Users2, Activity, Flag, ScrollText, Gauge } from "lucide-react";

// Platform-wide scope, distinct from both product-specific admin apps:
// manages organizations themselves (not their data), platform-wide user
// search across both products, system health, feature flags, audit log,
// and cross-product monitoring counters.
export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
}

export const CONSOLE_NAV: NavItem[] = [
  { label: "Organizations", href: "/organizations", icon: Building },
  { label: "Platform Users", href: "/users", icon: Users2 },
  { label: "System Health", href: "/health", icon: Activity },
  { label: "Feature Flags", href: "/config", icon: Flag },
  { label: "Audit Log", href: "/audit-log", icon: ScrollText },
  { label: "Monitoring", href: "/monitoring", icon: Gauge },
];
