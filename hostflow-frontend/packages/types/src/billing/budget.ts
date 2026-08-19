// Mirrors module-billing's SetBudgetRequest / BudgetVarianceResponse.

import type { ExpenseCategory } from "./expense";

export interface SetBudgetRequest {
  propertyId?: string;
  category: ExpenseCategory;
  budgetMonth: string; // ISO date, first of month
  allocatedAmount: string;
}

export interface BudgetVarianceResponse {
  propertyId: string | null;
  category: ExpenseCategory;
  budgetMonth: string;
  allocatedAmount: string;
  actualSpent: string;
  variance: string;
}
