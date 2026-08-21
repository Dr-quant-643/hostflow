import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";
import { createAuthConfig } from "@hostflow/auth/src/config";

const xanuosAdminConfig = createAuthConfig("XANUOS_ADMIN", "/xanuos-admin");
const nazilcoAdminConfig = createAuthConfig("NAZILCO_ADMIN", "/nazilco-admin");

// Both workspaces are protect-by-default (matches both old admin apps' policy).
const XANUOS_ADMIN_PUBLIC_PATHS = ["/xanuos-admin/api/auth", "/xanuos-admin/access-denied"];
const NAZILCO_ADMIN_PUBLIC_PATHS = ["/nazilco-admin/api/auth", "/nazilco-admin/access-denied"];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  if (pathname === "/") return NextResponse.next();

  if (pathname.startsWith("/xanuos-admin")) {
    return authorityGate(
      request,
      { publicPaths: XANUOS_ADMIN_PUBLIC_PATHS, requireAnyAuthority: ["PRODUCT_XANUOS"] },
      xanuosAdminConfig,
    );
  }

  if (pathname.startsWith("/nazilco-admin")) {
    return authorityGate(
      request,
      { publicPaths: NAZILCO_ADMIN_PUBLIC_PATHS, requireAnyAuthority: ["PRODUCT_NAZILCO"] },
      nazilcoAdminConfig,
    );
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
