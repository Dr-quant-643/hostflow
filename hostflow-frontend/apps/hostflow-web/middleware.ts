import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";

const PUBLIC_PATHS = ["/login", "/api/auth", "/access-denied"];

export function middleware(request: NextRequest) {
  return authorityGate(request, {
    publicPaths: PUBLIC_PATHS,
    requireAnyAuthority: ["PRODUCT_XANUOS"],
  });
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
