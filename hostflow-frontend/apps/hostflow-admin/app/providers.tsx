"use client";

import { ThemeProvider } from "@hostflow/ui/src/providers/theme-provider";
import { ApiClientProvider } from "@hostflow/api-client/src/query-client";
import { SessionProvider } from "@hostflow/auth/src/use-session";
import { Toaster } from "@hostflow/ui";
import type { SessionUser } from "@hostflow/types";

export function Providers({
  children,
  initialUser,
}: {
  children: React.ReactNode;
  initialUser: SessionUser | null;
}) {
  return (
    <ThemeProvider>
      <SessionProvider initialUser={initialUser}>
        <ApiClientProvider>
          {children}
          <Toaster />
        </ApiClientProvider>
      </SessionProvider>
    </ThemeProvider>
  );
}
