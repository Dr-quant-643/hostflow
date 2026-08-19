"use client";

import { Topbar } from "@hostflow/ui";
import { useSession } from "@hostflow/auth/src/use-session";

export function AppTopbar() {
  const { user } = useSession();

  return (
    <Topbar
      userName={user?.name ?? "..."}
      userEmail={user?.email ?? ""}
      onLogout={() => {
        window.location.href = "/api/auth/logout";
      }}
    />
  );
}
