import type { LucideIcon } from "lucide-react";
import {
  LayoutDashboard,
  Building2,
  CalendarCheck,
  Users,
  Megaphone,
  Receipt,
  Wallet,
  PiggyBank,
  BarChart3,
  Bell,
  Settings,
  Wrench,
  KeyRound,
  Building,
  Store,
  UsersRound,
  Webhook,
} from "lucide-react";

export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
  authority?: string;
}

export const XANUOS_NAV: NavItem[] = [
  { label: "Dashboard", href: "/xanuos/dashboard", icon: LayoutDashboard },
  { label: "Properties", href: "/xanuos/properties", icon: Building2 },
  { label: "Bookings", href: "/xanuos/bookings", icon: CalendarCheck },
  { label: "CRM", href: "/xanuos/crm", icon: Users },
  { label: "Marketing", href: "/xanuos/marketing", icon: Megaphone },
  { label: "Billing", href: "/xanuos/billing", icon: Receipt },
  { label: "Expenses", href: "/xanuos/expenses", icon: Wallet },
  { label: "Budgets", href: "/xanuos/budgets", icon: PiggyBank },
  { label: "Maintenance", href: "/xanuos/maintenance", icon: Wrench },
  { label: "Rental", href: "/xanuos/rental", icon: KeyRound },
  { label: "Office", href: "/xanuos/office", icon: Building },
  { label: "Mall", href: "/xanuos/mall", icon: Store },
  { label: "My Team", href: "/xanuos/team", icon: UsersRound },
  { label: "Analytics", href: "/xanuos/analytics", icon: BarChart3 },
  { label: "Notifications", href: "/xanuos/notifications", icon: Bell },
  { label: "Developer", href: "/xanuos/developer", icon: Webhook },
  { label: "Settings", href: "/xanuos/settings", icon: Settings },
];
