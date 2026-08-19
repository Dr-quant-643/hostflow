// Gateway base URL. Per the integration guide: dev talks to
// http://localhost:8085/api/v1. Overridable per-environment via
// NEXT_PUBLIC_API_BASE_URL so staging/prod builds don't hardcode dev.

export function getApiBaseUrl(): string {
  const fromEnv =
    typeof process !== "undefined"
      ? process.env.NEXT_PUBLIC_API_BASE_URL
      : undefined;
  return fromEnv || "http://localhost:8085/api/v1";
}
