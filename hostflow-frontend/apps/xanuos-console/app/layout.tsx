import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { Providers } from "./providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "Console | RvanaFlow",
  description: "Platform-wide super-admin console",
};

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession();

  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers initialUser={session ?? null}>{children}</Providers>
      </body>
    </html>
  );
}
