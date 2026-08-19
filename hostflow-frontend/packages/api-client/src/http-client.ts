import type { ApiResponse } from "@hostflow/types";
import { getApiBaseUrl } from "./config";
import { ApiError } from "./errors";
import { csrfHeaders } from "./csrf";
import { tenantHeaders } from "./tenant-context";

export interface RequestOptions {
  method?: "GET" | "POST" | "PATCH" | "PUT" | "DELETE";
  body?: unknown;
  headers?: Record<string, string>;
  signal?: AbortSignal;
  /** Retry count for idempotent (GET) requests on network failure. Default 2. */
  retries?: number;
  /** Query string params. undefined/null values are omitted entirely. */
  params?: Record<string, string | number | boolean | undefined | null>;
}

function buildQueryString(
  params: RequestOptions["params"],
): string {
  if (!params) return "";
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null) {
      search.set(key, String(value));
    }
  }
  const query = search.toString();
  return query ? `?${query}` : "";
}

async function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Core typed fetch wrapper. Unwraps ApiResponse<T>, throws ApiError on any
 * failure path, injects CSRF + tenant headers, and retries idempotent GETs
 * on transient network failure with exponential backoff.
 *
 * Auth (Authorization: Bearer <JWT>) is intentionally NOT injected here —
 * that's owned by packages/auth (Module 6) via a thin wrapper, since the
 * BFF pattern means the browser talks to Next.js API routes, not the
 * gateway directly, and the JWT never touches client-side JS.
 */
export async function apiFetch<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const { method = "GET", body, headers = {}, signal, retries = 2, params } = options;
  const url = `${getApiBaseUrl()}${path}${buildQueryString(params)}`;

  const requestInit: RequestInit = {
    method,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...csrfHeaders(method),
      ...tenantHeaders(),
      ...headers,
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
    signal,
  };

  let attempt = 0;
  const maxAttempts = method === "GET" ? retries + 1 : 1;

  while (true) {
    attempt++;
    let response: Response;
    try {
      response = await fetch(url, requestInit);
    } catch (err) {
      if (attempt < maxAttempts) {
        await sleep(2 ** attempt * 100);
        continue;
      }
      throw ApiError.network(
        err instanceof Error ? err.message : "network error",
      );
    }

    let envelope: ApiResponse<T>;
    try {
      envelope = await response.json();
    } catch {
      throw new ApiError(
        "Received an invalid response from the server.",
        response.status,
      );
    }

    if (!envelope.success) {
      throw ApiError.fromEnvelope(response.status, envelope.error);
    }

    return envelope.data;
  }
}

/**
 * Multipart upload variant of apiFetch — deliberately separate rather than a
 * body-type branch inside apiFetch, since a FormData body must NOT get a
 * manual Content-Type: application/json header (the browser sets its own
 * multipart/form-data boundary header when the body is a FormData instance).
 */
export async function apiUpload<T>(
  path: string,
  formData: FormData,
  options: Omit<RequestOptions, "method" | "body"> = {},
): Promise<T> {
  const { headers = {}, signal, params } = options;
  const url = `${getApiBaseUrl()}${path}${buildQueryString(params)}`;

  let response: Response;
  try {
    response = await fetch(url, {
      method: "POST",
      credentials: "include",
      headers: {
        Accept: "application/json",
        ...csrfHeaders("POST"),
        ...tenantHeaders(),
        ...headers,
      },
      body: formData,
      signal,
    });
  } catch (err) {
    throw ApiError.network(err instanceof Error ? err.message : "network error");
  }

  let envelope: ApiResponse<T>;
  try {
    envelope = await response.json();
  } catch {
    throw new ApiError("Received an invalid response from the server.", response.status);
  }

  if (!envelope.success) {
    throw ApiError.fromEnvelope(response.status, envelope.error);
  }

  return envelope.data;
}

export const api = {
  get: <T>(path: string, options?: Omit<RequestOptions, "method" | "body">) =>
    apiFetch<T>(path, { ...options, method: "GET" }),
  post: <T>(
    path: string,
    body?: unknown,
    options?: Omit<RequestOptions, "method" | "body">,
  ) => apiFetch<T>(path, { ...options, method: "POST", body }),
  patch: <T>(
    path: string,
    body?: unknown,
    options?: Omit<RequestOptions, "method" | "body">,
  ) => apiFetch<T>(path, { ...options, method: "PATCH", body }),
  put: <T>(
    path: string,
    body?: unknown,
    options?: Omit<RequestOptions, "method" | "body">,
  ) => apiFetch<T>(path, { ...options, method: "PUT", body }),
  delete: <T>(
    path: string,
    options?: Omit<RequestOptions, "method" | "body">,
  ) => apiFetch<T>(path, { ...options, method: "DELETE" }),
};
