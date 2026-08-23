import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { PublicPropertySummary, PropertyType } from "@hostflow/types";

// All /properties/public/** endpoints return plain arrays (limit/offset,
// not page/size) — PublicPropertyQueries is a flat cross-tenant JDBC
// projection, not the paginated Spring Data PropertyResponse used on the
// staff side. No PublicPropertyDetail type: the detail endpoint returns the
// exact same PublicPropertyRow shape as the list/search endpoints — photos
// are fetched separately via usePropertyPhotos.

export function useDiscoverProperties(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["public", "properties", "discover", limit, offset],
    queryFn: () =>
      api.get<PublicPropertySummary[]>("/properties/public", {
        params: { limit, offset },
      }),
  });
}

export interface PropertySearchParams {
  city?: string;
  propertyType?: PropertyType;
  minPrice?: string;
  maxPrice?: string;
  limit?: number;
  offset?: number;
  [key: string]: string | number | boolean | undefined;
}

export function useSearchProperties(params: PropertySearchParams | null) {
  return useQuery({
    queryKey: ["public", "properties", "search", params],
    queryFn: () =>
      api.get<PublicPropertySummary[]>("/properties/public/search", {
        params: params ?? undefined,
      }),
    enabled: !!params,
  });
}

export function usePublicProperty(id: string) {
  return useQuery({
    queryKey: ["public", "properties", "detail", id],
    queryFn: () => api.get<PublicPropertySummary>(`/properties/public/${id}`),
    enabled: !!id,
  });
}

// Photo URLs are S3/MinIO presigned URLs that expire in 1 hour server-side
// (see PropertyDocumentService) — with no refresh, a property page left open
// past that point would silently start showing broken images. Refetching
// well inside that window (here: 45 min) keeps the URLs valid for as long as
// the query stays mounted, regardless of whether the tab was refocused.
const PRESIGNED_URL_REFRESH_INTERVAL_MS = 45 * 60 * 1000;

export function usePropertyPhotos(id: string) {
  return useQuery({
    queryKey: ["public", "properties", id, "photos"],
    queryFn: () => api.get<string[]>(`/properties/public/${id}/photos`),
    enabled: !!id,
    refetchInterval: PRESIGNED_URL_REFRESH_INTERVAL_MS,
  });
}

export function usePropertyVideos(id: string) {
  return useQuery({
    queryKey: ["public", "properties", id, "videos"],
    queryFn: () => api.get<string[]>(`/properties/public/${id}/videos`),
    enabled: !!id,
    refetchInterval: PRESIGNED_URL_REFRESH_INTERVAL_MS,
  });
}

// Real backend only supports "is this exact range free" (a single boolean),
// not a month-grid of per-day availability — there is no day-by-day
// availability endpoint anywhere in PublicPropertyController/
// GuestBookingController.
export function useCheckAvailability(
  propertyId: string,
  checkIn: string,
  checkOut: string,
) {
  return useQuery({
    queryKey: ["public", "bookings", "availability", propertyId, checkIn, checkOut],
    queryFn: () =>
      api.get<boolean>("/bookings/public/availability", {
        params: { propertyId, checkIn, checkOut },
      }),
    enabled: !!propertyId && !!checkIn && !!checkOut,
  });
}
