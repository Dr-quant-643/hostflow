// Synchronizer Token Pattern (per the frontend architecture doc, section 7):
// backend/BFF sets an XSRF-TOKEN cookie; every mutating request must echo
// it back as the X-XSRF-TOKEN header.

export function readCsrfCookie(): string | null {
  if (typeof document === "undefined") return null;
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
  // The capture group is never actually optional here (the regex has
  // exactly one, non-optional group), but TS's RegExpMatchArray typing
  // marks indexed access as possibly-undefined regardless — this is what
  // was failing `next build`'s type-check step (not a memory issue like
  // the earlier failed build attempts).
  return match?.[1] !== undefined ? decodeURIComponent(match[1]) : null;
}

export function csrfHeaders(method: string): Record<string, string> {
  const safe = method === "GET" || method === "HEAD" || method === "OPTIONS";
  if (safe) return {};
  const token = readCsrfCookie();
  return token ? { "X-XSRF-TOKEN": token } : {};
}
