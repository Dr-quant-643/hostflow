import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { Providers } from "./providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "Admin | HostFlow",
  description: "XanuOS support, billing, and product administration",
};

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // Fully authenticated app like hostflow-web (not optional like
  // nazilco-web) — an anonymous visitor here is always redirected.
  const session = await getServerSession();

  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers initialUser={session ?? null}>{children}</Providers>
      </body>
    </html>
  );
}
