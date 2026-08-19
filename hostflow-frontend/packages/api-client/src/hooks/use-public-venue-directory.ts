import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { StoreDirectoryEntry, PublicMeetingRoom } from "@hostflow/types";

// Both endpoints are anonymous/public (PublicVenueDirectoryController has no
// @PreAuthorize) — a visitor doesn't need an account to browse a mall's
// stores or see an office's meeting rooms.
export function useMallStoreDirectory(propertyId: string) {
  return useQuery({
    queryKey: ["public", "mall", "store-directory", propertyId],
    queryFn: () =>
      api.get<StoreDirectoryEntry[]>("/mall/public/store-directory", {
        params: { propertyId },
      }),
    enabled: !!propertyId,
  });
}

export function useOfficePublicRooms(propertyId: string) {
  return useQuery({
    queryKey: ["public", "office", "rooms", propertyId],
    queryFn: () =>
      api.get<PublicMeetingRoom[]>("/office/public/rooms", {
        params: { propertyId },
      }),
    enabled: !!propertyId,
  });
}
