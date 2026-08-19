"use client";

import { useState } from "react";
import { PageHeader, Input, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { BudgetForm } from "@/components/billing/budget-form";
import { BudgetVarianceTable } from "@/components/billing/budget-variance-table";

function firstOfMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-01`;
}

export default function BudgetsPage() {
  const { data: properties, isLoading } = useProperties();
  const [month, setMonth] = useState(firstOfMonth());

  return (
    <Stack gap="lg">
      <PageHeader title="Budgets" description="Set monthly budgets and track variance" />

      {isLoading && <Skeleton className="h-10 w-64" />}
      {!isLoading && (!properties || properties.length === 0) && (
        <EmptyState
          title="No properties yet"
          description="Add a property first before setting a budget."
        />
      )}
      {!isLoading && properties && properties.length > 0 && (
        <BudgetForm properties={properties} />
      )}

      <Input
        label="Month"
        type="date"
        value={month}
        onChange={(e) => setMonth(e.target.value)}
      />
      <BudgetVarianceTable month={month} />
    </Stack>
  );
}
