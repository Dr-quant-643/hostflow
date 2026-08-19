import type { LucideIcon } from "lucide-react";
import { LifeBuoy, Receipt, ShieldCheck } from "lucide-react";

// This app's scope per the continuity report: "support/billing/product
// admin" — distinct from xanuos-console (Module 12), which is "platform-
// wide super-admin". Keeping nav intentionally narrow to that scope rather
// than duplicating console-level capabilities here.
//
// Products/Plans deliberately has NO nav item — Products/Plans/Subscriptions
// are deferred to the very end of the project alongside MPESA (see
// hostflow.md §14). Re-add once that phase starts.
export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
}

export const ADMIN_NAV: NavItem[] = [
  { label: "Support", href: "/support", icon: LifeBuoy },
  { label: "Billing", href: "/billing", icon: Receipt },
  { label: "Access Control", href: "/access", icon: ShieldCheck },
];
