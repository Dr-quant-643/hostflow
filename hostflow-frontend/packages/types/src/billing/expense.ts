// Mirrors module-billing's ExpenseResponse / CreateExpenseRequest.

export type ExpenseCategory =
  | "MAINTENANCE"
  | "UTILITIES"
  | "STAFF"
  | "MARKETING"
  | "INSURANCE"
  | "TAXES"
  | "SUPPLIES"
  | "OTHER";

export interface ExpenseResponse {
  id: string;
  propertyId: string | null;
  category: ExpenseCategory;
  description: string;
  amount: string; // BigDecimal as string
  expenseDate: string;
}

export interface CreateExpenseRequest {
  propertyId?: string;
  category: ExpenseCategory;
  description: string;
  amount: string;
  expenseDate: string;
}
