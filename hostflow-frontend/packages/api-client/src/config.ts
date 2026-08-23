// The Gateway only understands JWT Bearer tokens, never this app's session
// cookie, so client-side code must never call it directly — it has to go
// through this app's own same-origin BFF proxy route (createGatewayProxyRoute,
// mounted per-product at e.g. app/xanuos/api/v1/[...path]/route.ts), which
// reads the session server-side and attaches the Bearer token itself. In the
// browser, that proxy path depends on which product's pages are currently
// rendering (a merged app hosts both /xanuos and /nazilco), so it's resolved
// from the current pathname rather than a single static base URL.
//
// NEXT_PUBLIC_API_BASE_URL remains a dev-only escape hatch for pointing
// directly at a locally-running Gateway without the proxy in the loop — it is
// deliberately NOT honored in the browser once a product prefix is known, so
// a stale/misconfigured value can't silently bypass the proxy (and with it,
// auth) in a deployed environment.
export function getApiBaseUrl(): string {
  if (typeof window !== "undefined") {
    if (window.location.pathname.startsWith("/nazilco")) return "/nazilco/api/v1";
    if (window.location.pathname.startsWith("/xanuos")) return "/xanuos/api/v1";
  }
  const fromEnv =
    typeof process !== "undefined"
      ? process.env.NEXT_PUBLIC_API_BASE_URL
      : undefined;
  return fromEnv || "http://localhost:8085/api/v1";
}
