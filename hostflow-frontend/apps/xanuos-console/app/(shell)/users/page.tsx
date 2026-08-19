"use client";

import { useState } from "react";
import {
  PageHeader,
  Input,
  DataTable,
  Skeleton,
  EmptyState,
  Badge,
  Stack,
} from "@hostflow/ui";
import { useFilteredPlatformUsers } from "@hostflow/api-client/src/hooks/use-console-users";
import type { PlatformUserRow } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const columns: ColumnDef<PlatformUserRow>[] = [
  {
    id: "name",
    header: "Name",
    cell: ({ row }) => `${row.original.firstName} ${row.original.lastName}`,
  },
  { accessorKey: "email", header: "Email" },
  { accessorKey: "organizationName", header: "Organization" },
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

export default function PlatformUsersPage() {
  const [query, setQuery] = useState("");
  const { data, isLoading, isError } = useFilteredPlatformUsers(query);

  return (
    <Stack gap="md">
      <PageHeader
        title="Platform Users"
        description="All users across every organization"
      />
      <Input
        placeholder="Filter by name or email..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load platform users" description="Try refreshing." />
      )}
      {!isLoading && data && data.length === 0 && <EmptyState title="No users found" />}
      {!isLoading && data && data.length > 0 && <DataTable columns={columns} data={data} />}
    </Stack>
  );
}
