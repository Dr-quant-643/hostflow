import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { createAuthConfig } from "@hostflow/auth/src/config";
import { Providers } from "../providers";

export const metadata: Metadata = {
  title: "XanuOS Admin | HostFlow",
  description: "XanuOS support, billing, and product administration",
};

const xanuosAdminConfig = createAuthConfig("XANUOS_ADMIN", "/xanuos-admin");

export default async function XanuosAdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // Fully authenticated workspace — an anonymous visitor here is always redirected.
  const session = await getServerSession(xanuosAdminConfig);

  return <Providers initialUser={session ?? null}>{children}</Providers>;
}
