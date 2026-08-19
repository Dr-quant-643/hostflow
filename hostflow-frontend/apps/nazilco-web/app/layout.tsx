import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { Providers } from "./providers";
import { SiteHeader } from "@/components/site-header";
import "./globals.css";

export const metadata: Metadata = {
  title: "NazilCo | Find your stay",
  description: "Discover and book properties",
};

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession().catch(() => null);

  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers initialUser={session ?? null}>
          <SiteHeader />
          <main>{children}</main>
        </Providers>
      </body>
    </html>
  );
}
