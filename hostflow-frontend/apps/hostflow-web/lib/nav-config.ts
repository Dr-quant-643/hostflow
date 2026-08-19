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
} from "lucide-react";

export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
  authority?: string;
}

export const XANUOS_NAV: NavItem[] = [
  { label: "Dashboard", href: "/dashboard", icon: LayoutDashboard },
  { label: "Properties", href: "/properties", icon: Building2 },
  { label: "Bookings", href: "/bookings", icon: CalendarCheck },
  { label: "CRM", href: "/crm", icon: Users },
  { label: "Marketing", href: "/marketing", icon: Megaphone },
  { label: "Billing", href: "/billing", icon: Receipt },
  { label: "Expenses", href: "/expenses", icon: Wallet },
  { label: "Budgets", href: "/budgets", icon: PiggyBank },
  { label: "Maintenance", href: "/maintenance", icon: Wrench },
  { label: "Rental", href: "/rental", icon: KeyRound },
  { label: "Office", href: "/office", icon: Building },
  { label: "Mall", href: "/mall", icon: Store },
  { label: "My Team", href: "/team", icon: UsersRound },
  { label: "Analytics", href: "/analytics", icon: BarChart3 },
  { label: "Notifications", href: "/notifications", icon: Bell },
  { label: "Settings", href: "/settings", icon: Settings },
];
