"use client";

import { DataTable, Skeleton, EmptyState } from "@hostflow/ui";
import { useBudgetVariance } from "@hostflow/api-client/src/hooks/use-billing";
import type { BudgetVarianceResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<BudgetVarianceResponse>[] = [
  { accessorKey: "category", header: "Category" },
  {
    accessorKey: "allocatedAmount",
    header: "Allocated",
    cell: ({ row }) => `$${row.original.allocatedAmount}`,
  },
  {
    accessorKey: "actualSpent",
    header: "Actual Spent",
    cell: ({ row }) => `$${row.original.actualSpent}`,
  },
  {
    accessorKey: "variance",
    header: "Variance",
    cell: ({ row }) => {
      const variance = Number(row.original.variance);
      return (
        <span className={variance < 0 ? "text-destructive" : "text-success-700"}>
          ${row.original.variance}
        </span>
      );
    },
  },
];

export function BudgetVarianceTable({ month }: { month: string }) {
  const { data, isLoading, isError } = useBudgetVariance(month);

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load budget variance" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No budgets set for this month"
        description="Set a budget using the form above."
      />
    );
  }

  return <DataTable columns={columns} data={data} />;
}
