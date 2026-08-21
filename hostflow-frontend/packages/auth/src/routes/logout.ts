import { NextRequest, NextResponse } from "next/server";
import { SESSION_COOKIE_NAME } from "../session";

// Mount at apps/*/src/app/api/auth/logout/route.ts as:
//   export { POST } from "@hostflow/auth/routes/logout";
// Only clears the session cookie here and hands off to logout-finish for the
// CSRF cookie -- see session.ts for why no response here ever clears more
// than one cookie at a time.
export async function POST(request: NextRequest) {
  const response = NextResponse.redirect(
    new URL("/api/auth/logout-finish", request.url),
  );
  response.cookies.delete(SESSION_COOKIE_NAME);
  return response;
}
