"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useAllInvoices } from "@hostflow/api-client/src/hooks/use-admin-billing";
import type { InvoiceSummaryRow } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<InvoiceSummaryRow>[] = [
  { accessorKey: "organizationName", header: "Organization" },
  {
    accessorKey: "amount",
    header: "Amount",
    cell: ({ row }) => `$${row.original.amount}`,
  },
  { accessorKey: "dueDate", header: "Due Date" },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function AdminInvoiceList() {
  const { data, isLoading, isError } = useAllInvoices();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load cross-org invoices" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No invoices found" />;
  }

  return <DataTable columns={columns} data={data} />;
}
