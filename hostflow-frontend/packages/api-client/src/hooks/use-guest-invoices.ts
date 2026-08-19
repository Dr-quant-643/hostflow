import { useQuery } from "@tanstack/react-query";
import { api } from "../http-client";
import type { GuestInvoiceRow } from "@hostflow/types";

// GET /api/v1/invoices/mine (GuestInvoiceController) — plain list, ordered
// by due_date DESC server-side.
export function useMyInvoices() {
  return useQuery({
    queryKey: ["guest-portal", "my-invoices"],
    queryFn: () => api.get<GuestInvoiceRow[]>("/invoices/mine"),
  });
}
