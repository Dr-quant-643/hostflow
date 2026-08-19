"use client";

import { DataTable, Skeleton, EmptyState, Badge, Select } from "@hostflow/ui";
import { toast } from "sonner";
import {
  useUsers,
  useUpdateUserAuthorities,
} from "@hostflow/api-client/src/hooks/use-access-control";
import type { User } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

const AVAILABLE_AUTHORITIES = [
  "PRODUCT_XANUOS",
  "PRODUCT_NAZILCO",
  "ROLE_PLATFORM_ADMIN",
];

export function UserAuthorityList() {
  const { data, isLoading, isError } = useUsers();
  const updateAuthorities = useUpdateUserAuthorities("");

  const columns: ColumnDef<User>[] = [
    { accessorKey: "name", header: "Name" },
    { accessorKey: "email", header: "Email" },
    {
      accessorKey: "authorities",
      header: "Authorities",
      cell: ({ row }) => (
        <div className="flex flex-wrap gap-1">
          {(row.original.authorities ?? []).map((a: string) => (
            <Badge key={a} variant="outline">
              {a}
            </Badge>
          ))}
        </div>
      ),
    },
  ];

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return (
      <EmptyState title="Couldn't load users" description="Try refreshing." />
    );
  }
  if (!data || data.content.length === 0) {
    return <EmptyState title="No users found" />;
  }

  return <DataTable columns={columns} data={data.content} />;
}
