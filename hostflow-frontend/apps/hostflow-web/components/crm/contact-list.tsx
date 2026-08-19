"use client";

import Link from "next/link";
import { DataTable, Skeleton, EmptyState, Button, Badge } from "@hostflow/ui";
import { useContacts } from "@hostflow/api-client/src/hooks/use-crm";
import type { ContactResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<ContactResponse>[] = [
  { accessorKey: "fullName", header: "Name" },
  { accessorKey: "email", header: "Email" },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => <Badge>{row.original.status}</Badge>,
  },
];

export function ContactList() {
  const { data, isLoading, isError } = useContacts();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState
        title="Couldn't load contacts"
        description="Try refreshing."
      />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No contacts yet"
        description="Contacts appear here as leads and guests come in."
        action={
          <Button asChild>
            <Link href="/crm/new">Add Contact</Link>
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
        window.location.href = `/crm/${row.id}`;
      }}
    />
  );
}
