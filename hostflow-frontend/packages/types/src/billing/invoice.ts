// Mirrors module-billing's InvoiceResponse / CreateInvoiceRequest /
// BatchCreateInvoicesRequest / BatchCreateInvoicesResponse.

export type InvoiceStatus = "DRAFT" | "ISSUED" | "PAID" | "OVERDUE" | "VOID";

export interface InvoiceResponse {
  id: string;
  bookingId: string;
  billedUserId: string;
  amount: string; // BigDecimal as string
  dueDate: string;
  status: InvoiceStatus;
}

export interface CreateInvoiceRequest {
  bookingId: string;
  billedUserId: string;
  amount: string;
  dueDate: string;
}

export interface BatchCreateInvoicesRequest {
  invoices: CreateInvoiceRequest[];
}

export interface BatchInvoiceResult {
  index: number;
  success: boolean;
  invoiceId?: string;
  errorMessage?: string;
}

export interface BatchCreateInvoicesResponse {
  totalRequested: number;
  succeeded: number;
  failed: number;
  results: BatchInvoiceResult[];
}
