"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion } from "framer-motion";
import { Button, Stack } from "@hostflow/ui";
import { useSession } from "@hostflow/auth/src/use-session";
import { NAZILCO_NAV, NAZILCO_AUTHENTICATED_NAV } from "@/lib/nav-config";

function NavLink({ href, label }: { href: string; label: string }) {
  const pathname = usePathname();
  const active = pathname === href || pathname?.startsWith(`${href}/`);

  return (
    <Link href={href} className="group relative py-1 text-sm text-foreground/80 transition-colors hover:text-foreground">
      {label}
      <span
        className={`absolute -bottom-0.5 left-0 h-[1.5px] bg-primary transition-all duration-300 ${
          active ? "w-full" : "w-0 group-hover:w-full"
        }`}
      />
    </Link>
  );
}

export function SiteHeader() {
  const { user } = useSession();

  return (
    <motion.header
      initial={{ y: -16, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.4 }}
      className="sticky top-0 z-40 flex items-center justify-between border-b border-border/60 bg-background/80 px-6 py-3.5 backdrop-blur-md"
    >
      <Link href="/" className="flex items-center gap-1.5 text-lg font-semibold tracking-tight">
        <span className="bg-gradient-to-br from-sapphire-600 to-purple-600 bg-clip-text text-transparent">
          NazilCo
        </span>
      </Link>
      <nav className="hidden items-center gap-7 sm:flex">
        {NAZILCO_NAV.map((link) => (
          <NavLink key={link.href} href={link.href} label={link.label} />
        ))}
        {user &&
          NAZILCO_AUTHENTICATED_NAV.map((link) => (
            <NavLink key={link.href} href={link.href} label={link.label} />
          ))}
      </nav>
      {user ? (
        <Button
          variant="outline"
          size="sm"
          onClick={() => {
            // POST-only (state change, avoids cross-site GET-triggered logout)
            const form = document.createElement("form");
            form.method = "POST";
            form.action = "/api/auth/logout";
            document.body.appendChild(form);
            form.submit();
          }}
        >
          Log out
        </Button>
      ) : (
        <Stack direction="row" gap="sm" align="center">
          <Link href="/signup" className="text-sm text-foreground/80 hover:text-foreground">
            Sign up
          </Link>
          <Button
            size="sm"
            onClick={() => (window.location.href = "/api/auth/login")}
          >
            Log in
          </Button>
        </Stack>
      )}
    </motion.header>
  );
}
