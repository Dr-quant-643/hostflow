import type { NextRequest } from "next/server";
import { authorityGate } from "@hostflow/auth/src/middleware-guard";

// Inverse of the other 4 apps: everything is public by default (discovery,
// search, property details, mall/office directories, signup are all
// anonymous-browsable), and only guest-account paths require a PRODUCT_NAZILCO
// session.
export const PROTECTED_PATHS = [
  "/checkout",
  "/guest-portal",
  "/profile",
  "/invoices",
  "/notifications",
  "/support",
];

export function middleware(request: NextRequest) {
  return authorityGate(request, {
    protectedPaths: PROTECTED_PATHS,
    requireAnyAuthority: ["PRODUCT_NAZILCO"],
  });
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
