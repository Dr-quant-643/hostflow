import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { InvoiceSummaryRow } from "@hostflow/types";

// GET /api/v1/admin/billing/invoices (PlatformAdminController,
// hasRole('PLATFORM_ADMIN')) — backed by PlatformBillingQueries, a
// deliberate cross-tenant read via platformAdminJdbcTemplate. Plain array
// (limit/offset), not a paginated envelope.
export function useAllInvoices(status?: string, limit = 50, offset = 0) {
  return useQuery({
    queryKey: ["admin", "billing", "invoices", status, limit, offset],
    queryFn: () =>
      api.get<InvoiceSummaryRow[]>("/admin/billing/invoices", {
        params: { status, limit, offset },
      }),
  });
}
