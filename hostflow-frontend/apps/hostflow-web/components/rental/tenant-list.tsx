"use client";

import { DataTable, Skeleton, EmptyState } from "@hostflow/ui";
import { useRentalTenants } from "@hostflow/api-client/src/hooks/use-rental";
import type { RentalTenantResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<RentalTenantResponse>[] = [
  { accessorKey: "fullName", header: "Name" },
  { accessorKey: "email", header: "Email" },
  { accessorKey: "phone", header: "Phone" },
];

export function TenantList() {
  const { data, isLoading, isError } = useRentalTenants();

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load tenants" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No tenants yet" description="Add a tenant using the form above." />;
  }

  return <DataTable columns={columns} data={data} />;
}
