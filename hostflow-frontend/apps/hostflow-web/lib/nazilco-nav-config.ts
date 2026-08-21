export interface NavLink {
  label: string;
  href: string;
}

export const NAZILCO_NAV: NavLink[] = [
  { label: "Discover", href: "/nazilco/discover" },
  { label: "Search", href: "/nazilco/search" },
];

// Shown only when a session exists — swapped in by the header component,
// not merged into the public nav above.
export const NAZILCO_AUTHENTICATED_NAV: NavLink[] = [
  { label: "My Trips", href: "/nazilco/guest-portal" },
  { label: "Invoices", href: "/nazilco/invoices" },
  { label: "Notifications", href: "/nazilco/notifications" },
  { label: "Support", href: "/nazilco/support" },
  { label: "Profile", href: "/nazilco/profile" },
];
