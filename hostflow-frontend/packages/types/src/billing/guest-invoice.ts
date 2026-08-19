// Mirrors GuestInvoiceQueries.GuestInvoiceRow — a slimmer projection than
// module-billing's staff-facing InvoiceResponse (no tenant/line-item detail,
// just what a guest needs to see their own bill).

export interface GuestInvoiceRow {
  id: string;
  amount: string;
  dueDate: string;
  status: string;
}
