import type { LucideIcon } from "lucide-react";
import { LifeBuoy, CalendarCheck } from "lucide-react";

// Scope per the continuity report: "NazilCo support admin" only —
// narrower than hostflow-admin (no billing/products/access-control here).
export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
}

export const NAZILCO_ADMIN_NAV: NavItem[] = [
  { label: "Support", href: "/support", icon: LifeBuoy },
  { label: "Bookings Oversight", href: "/bookings", icon: CalendarCheck },
];
