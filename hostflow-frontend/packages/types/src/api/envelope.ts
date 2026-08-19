// Mirrors the backend's uniform ApiResponse<T> envelope (core-common module).
// Every gateway response is shaped like this — success/error are mutually
// exclusive per the backend contract.

export interface ApiErrorDetail {
  code?: string;
  message: string;
  fieldErrors?: Record<string, string>;
}

export interface ApiSuccessResponse<T> {
  success: true;
  data: T;
  timestamp: string;
}

export interface ApiFailureResponse {
  success: false;
  error: ApiErrorDetail;
  timestamp: string;
}

export type ApiResponse<T> = ApiSuccessResponse<T> | ApiFailureResponse;

// Pagination wrapper (core-common's PageResponse) — used wherever the
// backend returns a paged collection.
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
