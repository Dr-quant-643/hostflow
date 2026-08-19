// Mirrors module-billing's PaymentResponse / RecordPaymentRequest.

export type PaymentStatus = "PENDING" | "SUCCEEDED" | "FAILED" | "REFUNDED";

export interface PaymentResponse {
  id: string;
  invoiceId: string;
  amount: string; // BigDecimal as string
  providerReference: string | null;
  status: PaymentStatus;
}

export interface RecordPaymentRequest {
  invoiceId: string;
  amount: string;
  providerReference?: string;
}
