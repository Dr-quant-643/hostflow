"use client";

import { useState } from "react";
import { PageHeader, Select, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { useProperties } from "@hostflow/api-client/src/hooks/use-properties";
import { ExpenseForm } from "@/components/billing/expense-form";
import { ExpenseList } from "@/components/billing/expense-list";

export default function ExpensesPage() {
  const { data: properties, isLoading } = useProperties();
  const [propertyId, setPropertyId] = useState("");

  const selected = propertyId || properties?.[0]?.id || "";

  return (
    <Stack gap="lg">
      <PageHeader title="Expenses" description="Track spending per property" />

      {isLoading && <Skeleton className="h-10 w-64" />}
      {!isLoading && (!properties || properties.length === 0) && (
        <EmptyState
          title="No properties yet"
          description="Add a property first before recording expenses."
        />
      )}
      {!isLoading && properties && properties.length > 0 && (
        <>
          <Select
            label="Property"
            value={selected}
            onChange={(e) => setPropertyId(e.target.value)}
            options={properties.map((p) => ({ value: p.id, label: p.name }))}
          />
          <ExpenseForm propertyId={selected} />
          <ExpenseList propertyId={selected} />
        </>
      )}
    </Stack>
  );
}
