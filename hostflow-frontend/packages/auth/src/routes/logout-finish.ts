import { NextRequest, NextResponse } from "next/server";
import { buildLogoutUrl } from "../keycloak-client";
import { CSRF_COOKIE_NAME } from "../csrf";

// Mount at apps/*/src/app/api/auth/logout-finish/route.ts as:
//   export { GET } from "@hostflow/auth/routes/logout-finish";
// Only reachable as the second hop of logout.ts's redirect chain. Clears
// the CSRF cookie, then hands off to Keycloak to end the actual SSO
// session.
export async function GET(request: NextRequest) {
  const response = NextResponse.redirect(buildLogoutUrl());
  response.cookies.delete(CSRF_COOKIE_NAME);
  return response;
}
