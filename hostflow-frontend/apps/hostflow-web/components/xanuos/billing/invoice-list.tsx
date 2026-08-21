"use client";

import Link from "next/link";
import { DataTable, Skeleton, EmptyState, Button, Badge } from "@hostflow/ui";
import { useInvoices } from "@hostflow/api-client/src/hooks/use-billing";
import type { InvoiceResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<InvoiceResponse>[] = [
  {
    accessorKey: "id",
    header: "Invoice",
    cell: ({ row }) => row.original.id.slice(0, 8),
  },
  { accessorKey: "dueDate", header: "Due Date" },
  {
    accessorKey: "amount",
    header: "Amount",
    cell: ({ row }) => `$${row.original.amount}`,
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function InvoiceList() {
  const { data, isLoading, isError } = useInvoices();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Couldn't load invoices"
        description="Try refreshing."
      />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No invoices yet"
        description="Invoices appear here once bookings are billed."
        action={
          <Button asChild>
            <Link href="/billing/batch-import">Batch Import</Link>
          </Button>
        }
      />
    );
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/billing/${row.id}`;
      }}
    />
  );
}
