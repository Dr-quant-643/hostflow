import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { createAuthConfig } from "@hostflow/auth/src/config";
import { Providers } from "../providers";

export const metadata: Metadata = {
  title: "NazilCo Admin | RvanaFlow",
  description: "NazilCo guest support administration",
};

const nazilcoAdminConfig = createAuthConfig("NAZILCO_ADMIN", "/nazilco-admin");

export default async function NazilcoAdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession(nazilcoAdminConfig);

  return <Providers initialUser={session ?? null}>{children}</Providers>;
}
