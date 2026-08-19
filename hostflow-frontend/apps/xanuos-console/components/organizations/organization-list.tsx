"use client";

import { DataTable, Skeleton, EmptyState, Badge } from "@hostflow/ui";
import { useOrganizations } from "@hostflow/api-client/src/hooks/use-console-organizations";
import type { OrganizationRow } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<OrganizationRow>[] = [
  { accessorKey: "name", header: "Organization" },
  { accessorKey: "slug", header: "Slug" },
  {
    accessorKey: "primaryProduct",
    header: "Product",
    cell: ({ row }) => <Badge variant="outline">{row.original.primaryProduct}</Badge>,
  },
  {
    accessorKey: "active",
    header: "Status",
    cell: ({ row }) => (
      <Badge variant={row.original.active ? "success" : "outline"}>
        {row.original.active ? "Active" : "Inactive"}
      </Badge>
    ),
  },
];

export function OrganizationList() {
  const { data, isLoading, isError } = useOrganizations();

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load organizations" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No organizations found" />;
  }

  return (
    <DataTable
      columns={columns}
      data={data}
      onRowClick={(row) => {
        window.location.href = `/organizations/${row.id}`;
      }}
    />
  );
}
