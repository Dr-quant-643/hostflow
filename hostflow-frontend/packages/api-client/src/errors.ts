import type { ApiErrorDetail } from "@hostflow/types";

// Normalized error thrown by the http client for every non-success path
// (network failure, non-2xx, or success:false envelope) — app code and
// TanStack Query's error handling only ever see this one shape.
export class ApiError extends Error {
  readonly status: number;
  readonly code?: string;
  readonly fieldErrors?: Record<string, string>;

  constructor(
    message: string,
    status: number,
    code?: string,
    fieldErrors?: Record<string, string>,
  ) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.fieldErrors = fieldErrors;
  }

  static fromEnvelope(status: number, error: ApiErrorDetail): ApiError {
    return new ApiError(error.message, status, error.code, error.fieldErrors);
  }

  static network(originalMessage: string): ApiError {
    return new ApiError(
      "Something went wrong. Check your connection and try again.",
      0,
      "NETWORK_ERROR",
    );
  }

  get isAuthError(): boolean {
    return this.status === 401 || this.status === 403;
  }

  get isValidationError(): boolean {
    return this.status === 422 || !!this.fieldErrors;
  }
}
