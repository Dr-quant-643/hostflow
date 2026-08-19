import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { Providers } from "./providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "Admin | NazilCo",
  description: "NazilCo guest support administration",
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
