import type { Metadata } from "next";
import "./globals.css";

// Minimal root layout — no product-specific Providers here. See
// app/xanuos-admin/layout.tsx and app/nazilco-admin/layout.tsx for each
// admin workspace's own independent session.
export const metadata: Metadata = {
  title: "HostFlow Admin",
  description: "XanuOS and NazilCo administration",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>{children}</body>
    </html>
  );
}
