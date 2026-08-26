import type { Metadata } from "next";
import { Analytics } from "@vercel/analytics/next";
import "./globals.css";

// Minimal root layout — no product-specific Providers here. XanuOS and
// NazilCo are strictly separate identities (see app/xanuos/layout.tsx and
// app/nazilco/layout.tsx), so nothing product-scoped (SessionProvider,
// ApiClientProvider) belongs at this level; it would otherwise be shared
// between two sessions that must never touch.
export const metadata: Metadata = {
  title: "RvanaFlow",
  description: "Property and business operations platform",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        {children}
        <Analytics />
      </body>
    </html>
  );
}
