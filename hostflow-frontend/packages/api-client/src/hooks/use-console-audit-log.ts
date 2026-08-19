import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { AuditLogResponse, PageResponse } from "@hostflow/types";

// module-platform-admin's AuditLogController, hasRole('PLATFORM_ADMIN').
// Returns Spring Data's Page<AuditLogResponse> — content/totalElements/
// first/last line up with our PageResponse<T>.
export function useAuditLog(tenantId?: string, limit = 50, offset = 0) {
  return useQuery({
    queryKey: ["console", "audit-log", tenantId, limit, offset],
    queryFn: () =>
      api.get<PageResponse<AuditLogResponse>>("/admin/audit-log", {
        params: { tenantId, limit, offset },
      }),
  });
}
