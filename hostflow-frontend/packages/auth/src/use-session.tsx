"use client";

import * as React from "react";
import type { SessionUser } from "@hostflow/types";

interface SessionContextValue {
  user: SessionUser | null;
  isLoading: boolean;
}

const SessionContext = React.createContext<SessionContextValue>({
  user: null,
  isLoading: true,
});

// Server Component fetches the session (getServerSession) and passes it
// down as `initialUser` — this provider just makes it available to client
// components via a hook, with no extra client-side fetch on first paint.
export function SessionProvider({
  children,
  initialUser,
}: {
  children: React.ReactNode;
  initialUser: SessionUser | null;
}) {
  return (
    <SessionContext.Provider value={{ user: initialUser, isLoading: false }}>
      {children}
    </SessionContext.Provider>
  );
}

export function useSession() {
  return React.useContext(SessionContext);
}
