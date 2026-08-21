"use client";

import { DataTable, Skeleton, EmptyState } from "@hostflow/ui";
import { useExpenses } from "@hostflow/api-client/src/hooks/use-billing";
import type { ExpenseResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<ExpenseResponse>[] = [
  { accessorKey: "expenseDate", header: "Date" },
  { accessorKey: "category", header: "Category" },
  { accessorKey: "description", header: "Description" },
  {
    accessorKey: "amount",
    header: "Amount",
    cell: ({ row }) => `$${row.original.amount}`,
  },
];

export function ExpenseList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = useExpenses(propertyId);

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load expenses" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return (
      <EmptyState
        title="No expenses recorded yet"
        description="Record an expense for this property using the form above."
      />
    );
  }

  return <DataTable columns={columns} data={data.content} />;
}
