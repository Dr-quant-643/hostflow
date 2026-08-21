import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { createAuthConfig } from "@hostflow/auth/src/config";
import { Providers } from "../providers";
import { SiteHeader } from "@/components/nazilco/site-header";

export const metadata: Metadata = {
  title: "NazilCo | Find your stay",
  description: "Discover and book properties",
};

const nazilcoConfig = createAuthConfig("NAZILCO", "/nazilco");

export default async function NazilcoLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession(nazilcoConfig).catch(() => null);

  return (
    <Providers initialUser={session ?? null}>
      <SiteHeader />
      <main>{children}</main>
    </Providers>
  );
}
