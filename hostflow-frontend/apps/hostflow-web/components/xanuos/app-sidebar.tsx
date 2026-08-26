"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { XANUOS_NAV } from "@/lib/xanuos-nav-config";
import { useOwnerRentalInquiries } from "@hostflow/api-client/src/hooks/use-rental";

// Instagram-style unread count on the Notifications nav item -- an owner
// should be able to tell "there's something waiting" before ever opening the
// tab. Counts inquiries awaiting reply; extend here if other unread sources
// (e.g. the generic delivery log, once it tracks read state) join the badge.
function useNotificationBadgeCount() {
  const { data } = useOwnerRentalInquiries();
  return data?.filter((i) => i.status === "OPEN").length ?? 0;
}

function NavBadge({ count }: { count: number }) {
  if (count <= 0) return null;
  return (
    <span className="ml-auto flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1.5 text-[11px] font-semibold text-white">
      {count > 99 ? "99+" : count}
    </span>
  );
}

export function AppSidebar() {
  const pathname = usePathname();
  const badgeCount = useNotificationBadgeCount();

  return (
    <div className="flex h-full w-64 flex-col border-r border-border bg-card">
      <div className="flex h-16 items-center gap-2 border-b border-border px-5">
        <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-sapphire-500 to-purple-500 text-sm font-bold text-white shadow-md shadow-purple-500/20">
          X
        </span>
        <span className="bg-gradient-to-r from-sapphire-600 to-purple-600 bg-clip-text text-lg font-bold text-transparent dark:from-sapphire-400 dark:to-purple-400">
          XanuOS
        </span>
      </div>
      <nav className="flex-1 space-y-1 overflow-y-auto p-3">
        {XANUOS_NAV.map((item) => {
          const Icon = item.icon;
          const active = pathname.startsWith(item.href);
          return (
            <Link
              key={item.href}
              href={item.href}
              className={
                active
                  ? "flex items-center gap-3 rounded-lg bg-gradient-to-r from-sapphire-500 to-purple-500 px-3 py-2 text-sm font-medium text-white shadow-md shadow-purple-500/20 transition-all"
                  : "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-gradient-to-r hover:from-sapphire-50 hover:to-purple-50 hover:text-foreground dark:hover:from-sapphire-950/30 dark:hover:to-purple-950/30"
              }
            >
              <Icon className="h-4 w-4 shrink-0" />
              <span className="truncate">{item.label}</span>
              {item.href === "/xanuos/notifications" && <NavBadge count={badgeCount} />}
            </Link>
          );
        })}
      </nav>
    </div>
  );
}
