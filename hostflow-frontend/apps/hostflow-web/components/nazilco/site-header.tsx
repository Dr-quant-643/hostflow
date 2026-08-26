"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion } from "framer-motion";
import { Button, Stack } from "@hostflow/ui";
import { useSession } from "@hostflow/auth/src/use-session";
import { useMyRentalInquiries } from "@hostflow/api-client/src/hooks/use-rental";
import { NAZILCO_NAV, NAZILCO_AUTHENTICATED_NAV } from "@/lib/nazilco-nav-config";
import { UserMenu } from "@/components/nazilco/user-menu";

// Instagram-style unread count on the Notifications link -- a guest should
// see "there's something waiting" before ever opening the tab. Counts
// inquiries the owner has replied to.
function useNotificationBadgeCount(enabled: boolean) {
  const { data } = useMyRentalInquiries(enabled);
  return data?.filter((i) => i.status === "REPLIED").length ?? 0;
}

function NavLink({ href, label, badgeCount }: { href: string; label: string; badgeCount?: number }) {
  const pathname = usePathname();
  const active = pathname === href || pathname?.startsWith(`${href}/`);

  return (
    <Link href={href} className="group relative flex items-center gap-1.5 py-1 text-sm text-foreground/80 transition-colors hover:text-foreground">
      {label}
      {!!badgeCount && badgeCount > 0 && (
        <span className="flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1.5 text-[11px] font-semibold text-white">
          {badgeCount > 99 ? "99+" : badgeCount}
        </span>
      )}
      <span
        className={`absolute -bottom-0.5 left-0 h-[1.5px] bg-gradient-to-r from-sapphire-500 to-purple-500 transition-all duration-300 ${
          active ? "w-full" : "w-0 group-hover:w-full"
        }`}
      />
    </Link>
  );
}

export function SiteHeader() {
  const { user } = useSession();
  const badgeCount = useNotificationBadgeCount(!!user);

  return (
    <motion.header
      initial={{ y: -16, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.4 }}
      className="sticky top-0 z-40 flex items-center justify-between border-b border-border/60 bg-background/80 px-6 py-3.5 backdrop-blur-md"
    >
      <Link href="/nazilco" className="flex items-center gap-2 text-lg font-semibold tracking-tight">
        <span className="text-xl">📌</span>
        <span className="bg-gradient-to-br from-sapphire-500 via-purple-500 to-purple-600 bg-clip-text text-transparent">
          NazilCo
        </span>
      </Link>
      <nav className="hidden items-center gap-7 sm:flex">
        {NAZILCO_NAV.map((link) => (
          <NavLink key={link.href} href={link.href} label={link.label} />
        ))}
        {user &&
          NAZILCO_AUTHENTICATED_NAV.map((link) => (
            <NavLink
              key={link.href}
              href={link.href}
              label={link.label}
              badgeCount={link.href === "/nazilco/notifications" ? badgeCount : undefined}
            />
          ))}
      </nav>
      {user ? (
        <UserMenu
          user={user}
          onLogout={() => {
            // POST-only (state change, avoids cross-site GET-triggered logout)
            const form = document.createElement("form");
            form.method = "POST";
            form.action = "/nazilco/api/auth/logout";
            document.body.appendChild(form);
            form.submit();
          }}
        />
      ) : (
        <Stack direction="row" gap="sm" align="center">
          <Link href="/nazilco/signup" className="text-sm text-foreground/80 hover:text-foreground">
            Sign up
          </Link>
          <Button
            size="sm"
            className="bg-gradient-to-r from-sapphire-500 to-purple-600 hover:opacity-90"
            onClick={() => (window.location.href = "/nazilco/api/auth/login")}
          >
            Log in
          </Button>
        </Stack>
      )}
    </motion.header>
  );
}
