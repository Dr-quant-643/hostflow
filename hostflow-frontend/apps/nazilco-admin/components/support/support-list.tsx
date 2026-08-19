"use client";

import { DataTable, Skeleton, EmptyState, Button, Badge } from "@hostflow/ui";
import { toast } from "sonner";
import {
  useSupportTickets,
  useAssignTicket,
  useResolveTicket,
  useCloseTicket,
  useReopenTicket,
} from "@hostflow/api-client/src/hooks/use-support-tickets";
import { useSession } from "@hostflow/auth/src/use-session";
import type { SupportTicketResponse } from "@hostflow/types";
import { ColumnDef } from "@tanstack/react-table";

export function SupportList() {
  const { data, isLoading, isError } = useSupportTickets("NAZILCO");
  const assign = useAssignTicket();
  const resolve = useResolveTicket();
  const reopen = useReopenTicket();
  const close = useCloseTicket();
  const { user } = useSession();

  const columns: ColumnDef<SupportTicketResponse>[] = [
    { accessorKey: "subject", header: "Subject" },
    {
      accessorKey: "priority",
      header: "Priority",
      cell: ({ row }) => <Badge variant="outline">{row.original.priority}</Badge>,
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => <Badge>{row.original.status}</Badge>,
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => {
        const ticket = row.original;
        return (
          <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
            {!ticket.assignedToUserId && ticket.status !== "CLOSED" && (
              <Button
                size="sm"
                variant="outline"
                loading={assign.isPending}
                onClick={async () => {
                  try {
                    await assign.mutateAsync({ id: ticket.id, staffUserId: user?.id });
                    toast.success("Assigned to you");
                  } catch {
                    toast.error("Failed to assign");
                  }
                }}
              >
                Assign to me
              </Button>
            )}
            {(ticket.status === "OPEN" || ticket.status === "IN_PROGRESS") && (
              <Button
                size="sm"
                variant="outline"
                loading={resolve.isPending}
                onClick={async () => {
                  try {
                    await resolve.mutateAsync({ id: ticket.id });
                    toast.success("Marked resolved");
                  } catch {
                    toast.error("Failed to resolve");
                  }
                }}
              >
                Resolve
              </Button>
            )}
            {ticket.status === "RESOLVED" && (
              <>
                <Button
                  size="sm"
                  variant="outline"
                  loading={reopen.isPending}
                  onClick={async () => {
                    try {
                      await reopen.mutateAsync({ id: ticket.id });
                      toast.success("Reopened");
                    } catch {
                      toast.error("Failed to reopen");
                    }
                  }}
                >
                  Reopen
                </Button>
                <Button
                  size="sm"
                  loading={close.isPending}
                  onClick={async () => {
                    try {
                      await close.mutateAsync({ id: ticket.id });
                      toast.success("Closed");
                    } catch {
                      toast.error("Failed to close");
                    }
                  }}
                >
                  Close
                </Button>
              </>
            )}
          </div>
        );
      },
    },
  ];

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load support tickets" description="Try refreshing." />;
  }
  if (!data || data.content.length === 0) {
    return <EmptyState title="No support tickets" />;
  }

  return <DataTable columns={columns} data={data.content} />;
}
