import { NextRequest, NextResponse } from "next/server";
import { generateCsrfToken, CSRF_COOKIE_NAME } from "../csrf";

// Mount at apps/*/src/app/api/auth/finish/route.ts as:
//   export { GET } from "@hostflow/auth/routes/finish";
// Only reachable as the final hop of callback.ts's redirect chain. Exists
// solely to set the CSRF cookie in a response of its own -- see session.ts
// for why no response here ever sets more than one cookie at a time.
export async function GET(request: NextRequest) {
  const response = NextResponse.redirect(new URL("/", request.url));
  response.cookies.set(CSRF_COOKIE_NAME, generateCsrfToken(), {
    httpOnly: false, // must be readable by client JS to echo back per synchronizer pattern
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
  });
  return response;
}
