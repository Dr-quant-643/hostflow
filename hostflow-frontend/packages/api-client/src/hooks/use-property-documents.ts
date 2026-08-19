import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, apiUpload } from "../http-client";
import type { PropertyDocumentResponse, PropertyDocumentType } from "@hostflow/types";

// Same presigned-URL expiry concern as usePropertyPhotos (public side) —
// PropertyDocumentResponse.url is a 1-hour presigned S3/MinIO URL, so the
// owner's document list needs the same proactive refresh to avoid broken
// thumbnails/links on a long-open properties page.
const PRESIGNED_URL_REFRESH_INTERVAL_MS = 45 * 60 * 1000;

export function usePropertyDocuments(propertyId: string, documentType?: PropertyDocumentType) {
  return useQuery({
    queryKey: ["properties", propertyId, "documents", documentType ?? "all"],
    queryFn: () =>
      api.get<PropertyDocumentResponse[]>(`/properties/${propertyId}/documents`, {
        params: { documentType },
      }),
    enabled: !!propertyId,
    refetchInterval: PRESIGNED_URL_REFRESH_INTERVAL_MS,
  });
}

export function useUploadPropertyDocument(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ file, documentType }: { file: File; documentType: PropertyDocumentType }) => {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("documentType", documentType);
      return apiUpload<PropertyDocumentResponse>(
        `/properties/${propertyId}/documents`,
        formData,
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", propertyId, "documents"] });
    },
  });
}

export function useDeletePropertyDocument(propertyId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (documentId: string) =>
      api.delete<void>(`/properties/${propertyId}/documents/${documentId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["properties", propertyId, "documents"] });
    },
  });
}
