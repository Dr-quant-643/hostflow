"use client";

import { useEffect, useRef, useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { LogOut, User as UserIcon } from "lucide-react";
import type { SessionUser } from "@hostflow/types";

const AVATAR_GRADIENTS = [
  "from-purple-500 to-sapphire-500",
  "from-sapphire-500 to-purple-600",
  "from-purple-600 to-pink-500",
  "from-sapphire-600 to-purple-500",
];

// No profile photo is captured from Keycloak yet (see the Google
// sign-in follow-up) -- an initials avatar, colored deterministically per
// user, is the standard fallback other apps (Slack, Discord, GitHub) show
// until a real photo exists, rather than leaving a bare "Log out" label.
function initials(user: SessionUser): string {
  const source = user.name?.trim() || user.email?.trim() || "?";
  const parts = source.split(/\s+/).filter(Boolean);
  if (parts.length >= 2) return ((parts[0]?.[0] ?? "") + (parts[1]?.[0] ?? "")).toUpperCase();
  return source.slice(0, 2).toUpperCase();
}

function gradientFor(id: string): string {
  let hash = 0;
  for (let i = 0; i < id.length; i++) hash = (hash * 31 + id.charCodeAt(i)) >>> 0;
  return AVATAR_GRADIENTS[hash % AVATAR_GRADIENTS.length] ?? AVATAR_GRADIENTS[0]!;
}

export function UserMenu({ user, onLogout }: { user: SessionUser; onLogout: () => void }) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    }
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        aria-label="Account menu"
        aria-expanded={open}
        className={`flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br ${gradientFor(
          user.id,
        )} text-sm font-semibold text-white shadow-sm ring-2 ring-transparent transition-all hover:ring-purple-300/50`}
      >
        {initials(user)}
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -6, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -6, scale: 0.97 }}
            transition={{ duration: 0.15 }}
            className="absolute right-0 top-full z-50 mt-2 w-52 overflow-hidden rounded-xl border border-border/60 bg-background shadow-xl"
          >
            <div className="border-b border-border/60 px-3.5 py-3">
              <p className="truncate text-sm font-medium text-foreground">
                {user.name || "Your account"}
              </p>
              {user.email && (
                <p className="truncate text-xs text-muted-foreground">{user.email}</p>
              )}
            </div>
            <Link
              href="/nazilco/profile"
              onClick={() => setOpen(false)}
              className="flex items-center gap-2 px-3.5 py-2.5 text-sm text-foreground/80 hover:bg-muted"
            >
              <UserIcon className="h-4 w-4" />
              Profile
            </Link>
            <button
              type="button"
              onClick={() => {
                setOpen(false);
                onLogout();
              }}
              className="flex w-full items-center gap-2 px-3.5 py-2.5 text-left text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-950/20"
            >
              <LogOut className="h-4 w-4" />
              Log out
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
