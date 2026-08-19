export interface NavLink {
  label: string;
  href: string;
}

export const NAZILCO_NAV: NavLink[] = [
  { label: "Discover", href: "/discover" },
  { label: "Search", href: "/search" },
];

// Shown only when a session exists — swapped in by the header component,
// not merged into the public nav above.
export const NAZILCO_AUTHENTICATED_NAV: NavLink[] = [
  { label: "My Trips", href: "/guest-portal" },
  { label: "Invoices", href: "/invoices" },
  { label: "Notifications", href: "/notifications" },
  { label: "Support", href: "/support" },
  { label: "Profile", href: "/profile" },
];
