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
        // The route only accepts POST (logout is a state change, so GET
        // could otherwise be triggered cross-site e.g. via a stray <img>
        // tag) — a real form submit navigates the browser through the
        // POST -> Keycloak-logout redirect chain the same way a link would.
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "/xanuos/api/auth/logout";
        document.body.appendChild(form);
        form.submit();
      }}
    />
  );
}
