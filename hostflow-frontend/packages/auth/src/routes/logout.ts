import { NextRequest, NextResponse } from "next/server";
import { buildLogoutUrl } from "../keycloak-client";
import { SESSION_COOKIE_NAME, TOKEN_COOKIE_NAME } from "../session";
import { CSRF_COOKIE_NAME } from "../csrf";

// Mount at apps/*/src/app/api/auth/logout/route.ts as:
//   export { POST } from "@hostflow/auth/routes/logout";
export async function POST(request: NextRequest) {
  const response = NextResponse.redirect(buildLogoutUrl());
  response.cookies.delete(SESSION_COOKIE_NAME);
  response.cookies.delete(TOKEN_COOKIE_NAME);
  response.cookies.delete(CSRF_COOKIE_NAME);
  return response;
}
