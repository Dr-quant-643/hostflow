import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type {
  InvoiceResponse,
  BatchCreateInvoicesResponse,
  ExpenseResponse,
  BudgetVarianceResponse,
  PaymentResponse,
} from "@hostflow/types";
import type {
  BatchCreateInvoicesValues,
  ExpenseFormValues,
  BudgetFormValues,
  RecordPaymentFormValues,
} from "@hostflow/validation";

export function useInvoices(limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["billing", "invoices", "list", limit, offset],
    queryFn: () =>
      api.get<InvoiceResponse[]>("/billing/invoices", { params: { limit, offset } }),
  });
}

export function useInvoice(id: string) {
  return useQuery({
    queryKey: ["billing", "invoices", "detail", id],
    queryFn: () => api.get<InvoiceResponse>(`/billing/invoices/${id}`),
    enabled: !!id,
  });
}

export function useIssueInvoice(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<InvoiceResponse>(`/billing/invoices/${id}/issue`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["billing", "invoices", "list"] });
      queryClient.invalidateQueries({ queryKey: ["billing", "invoices", "detail", id] });
    },
  });
}

export function useMarkInvoicePaid(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<InvoiceResponse>(`/billing/invoices/${id}/pay`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["billing", "invoices", "list"] });
      queryClient.invalidateQueries({ queryKey: ["billing", "invoices", "detail", id] });
    },
  });
}

// Sync path only (≤100 rows) — the async RabbitMQ path for >100-row batches
// doesn't exist on the backend. Response carries a per-row success/failure
// report, not a plain array of created invoices.
export function useBatchCreateInvoices() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: BatchCreateInvoicesValues) =>
      api.post<BatchCreateInvoicesResponse>("/billing/invoices/batch", values),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["billing", "invoices", "list"],
      });
    },
  });
}

// ExpenseController.listByProperty requires propertyId — for an org-wide
// total, use useExpenseTotalByCategory below instead of listing everything.
export function useExpenses(propertyId: string, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["billing", "expenses", propertyId, limit, offset],
    queryFn: () =>
      api.get<{ content: ExpenseResponse[] }>("/billing/expenses", {
        params: { propertyId, limit, offset },
      }),
    enabled: !!propertyId,
  });
}

// Tenant-wide, all-time sum for one category (e.g. MAINTENANCE) -- backs
// dashboard tiles like "Maintenance cost" without listing every expense.
export function useExpenseTotalByCategory(category: string) {
  return useQuery({
    queryKey: ["billing", "expenses", "total-by-category", category],
    queryFn: () =>
      api.get<string>("/billing/expenses/total-by-category", { params: { category } }),
  });
}

export function useCreateExpense() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: ExpenseFormValues) =>
      api.post<ExpenseResponse>("/billing/expenses", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["billing", "expenses", variables.propertyId],
      });
    },
  });
}

export function useBudgetVariance(month: string) {
  return useQuery({
    queryKey: ["billing", "budgets", "variance", month],
    queryFn: () =>
      api.get<BudgetVarianceResponse[]>("/billing/budgets/variance", { params: { month } }),
    enabled: !!month,
  });
}

export function useSetBudget() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: BudgetFormValues) =>
      api.put<string>("/billing/budgets", values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["billing", "budgets", "variance"] });
    },
  });
}

export function usePaymentsByInvoice(invoiceId: string) {
  return useQuery({
    queryKey: ["billing", "payments", invoiceId],
    queryFn: () =>
      api.get<PaymentResponse[]>("/billing/payments", { params: { invoiceId } }),
    enabled: !!invoiceId,
  });
}

export function useRecordPayment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: RecordPaymentFormValues) =>
      api.post<PaymentResponse>("/billing/payments", values),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["billing", "payments", variables.invoiceId],
      });
    },
  });
}

function useUpdatePaymentStatus(action: "succeed" | "fail" | "refund", invoiceId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (paymentId: string) =>
      api.patch<PaymentResponse>(`/billing/payments/${paymentId}/${action}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["billing", "payments", invoiceId] });
    },
  });
}

export function useMarkPaymentSucceeded(invoiceId: string) {
  return useUpdatePaymentStatus("succeed", invoiceId);
}

export function useMarkPaymentFailed(invoiceId: string) {
  return useUpdatePaymentStatus("fail", invoiceId);
}

export function useRefundPayment(invoiceId: string) {
  return useUpdatePaymentStatus("refund", invoiceId);
}
