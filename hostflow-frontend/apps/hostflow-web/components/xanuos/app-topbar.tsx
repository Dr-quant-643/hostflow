"use client";

import { LogOut } from "lucide-react";
import { useSession } from "@hostflow/auth/src/use-session";

function initials(name: string) {
  const parts = name.trim().split(/\s+/);
  if (parts.length === 0 || !parts[0]) return "?";
  return (parts[0][0] + (parts[1]?.[0] ?? "")).toUpperCase();
}

export function AppTopbar() {
  const { user } = useSession();

  const handleLogout = () => {
    // The route only accepts POST (logout is a state change, so GET
    // could otherwise be triggered cross-site e.g. via a stray <img>
    // tag) — a real form submit navigates the browser through the
    // POST -> Keycloak-logout redirect chain the same way a link would.
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/xanuos/api/auth/logout";
    document.body.appendChild(form);
    form.submit();
  };

  return (
    <div className="flex h-16 items-center justify-between gap-4 border-b border-border bg-background/95 px-6 backdrop-blur">
      <div />
      <div className="flex items-center gap-3">
        <div className="flex items-center gap-3 border-l border-border pl-4">
          <div className="text-right leading-tight">
            <div className="text-sm font-medium text-foreground">{user?.name ?? "..."}</div>
            <div className="text-xs text-muted-foreground">{user?.email ?? ""}</div>
          </div>
          <span className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-sapphire-500 to-purple-500 text-xs font-bold text-white shadow-md shadow-purple-500/20">
            {initials(user?.name ?? "?")}
          </span>
          <button
            type="button"
            onClick={handleLogout}
            aria-label="Log out"
            className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-gradient-to-r hover:from-sapphire-50 hover:to-purple-50 hover:text-purple-600 dark:hover:from-sapphire-950/30 dark:hover:to-purple-950/30"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
