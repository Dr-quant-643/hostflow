import type { Metadata } from "next";
import { getServerSession } from "@hostflow/auth/src/server";
import { createAuthConfig } from "@hostflow/auth/src/config";
import { Providers } from "../providers";

export const metadata: Metadata = {
  title: "XanuOS | RvanaFlow",
  description: "Property and business operations platform",
};

const xanuosConfig = createAuthConfig("XANUOS", "/xanuos");

export default async function XanuosLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const session = await getServerSession(xanuosConfig);

  return <Providers initialUser={session ?? null}>{children}</Providers>;
}
