"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, Input, Textarea, Card } from "@hostflow/ui";
import {
  useWorkOrder,
  useAssignTechnician,
  useStartWorkOrder,
  useCompleteWorkOrder,
  useCancelWorkOrder,
} from "@hostflow/api-client/src/hooks/use-maintenance";

export default function WorkOrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: wo, isLoading, isError } = useWorkOrder(id);
  const assign = useAssignTechnician(id);
  const start = useStartWorkOrder(id);
  const complete = useCompleteWorkOrder(id);
  const cancel = useCancelWorkOrder(id);

  const [technicianUserId, setTechnicianUserId] = useState("");
  const [resolutionNotes, setResolutionNotes] = useState("");

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !wo) return <EmptyState title="Work order not found" />;

  return (
    <Stack gap="lg">
      <PageHeader
        title={wo.title}
        description={wo.category}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge>{wo.priority}</Badge>
            <Badge>{wo.status}</Badge>
          </Stack>
        }
      />
      {wo.description && <p>{wo.description}</p>}

      {wo.status === "OPEN" && (
        <Card>
          <Stack direction="row" gap="sm" align="end">
            <Input
              label="Technician User ID"
              value={technicianUserId}
              onChange={(e) => setTechnicianUserId(e.target.value)}
            />
            <Button
              disabled={!technicianUserId.trim()}
              loading={assign.isPending}
              onClick={async () => {
                try {
                  await assign.mutateAsync(technicianUserId);
                  toast.success("Technician assigned");
                } catch {
                  toast.error("Failed to assign technician");
                }
              }}
            >
              Assign
            </Button>
          </Stack>
        </Card>
      )}

      {wo.status === "ASSIGNED" && (
        <Button
          loading={start.isPending}
          onClick={async () => {
            try {
              await start.mutateAsync();
              toast.success("Work started");
            } catch {
              toast.error("Failed to start work");
            }
          }}
        >
          Start Work
        </Button>
      )}

      {wo.status === "IN_PROGRESS" && (
        <Card>
          <Stack gap="md">
            <Textarea
              label="Resolution Notes"
              value={resolutionNotes}
              onChange={(e) => setResolutionNotes(e.target.value)}
            />
            <Button
              loading={complete.isPending}
              onClick={async () => {
                try {
                  await complete.mutateAsync(resolutionNotes);
                  toast.success("Work order completed");
                } catch {
                  toast.error("Failed to complete work order");
                }
              }}
            >
              Complete
            </Button>
          </Stack>
        </Card>
      )}

      {wo.status !== "COMPLETED" && wo.status !== "CANCELLED" && (
        <Button
          variant="destructive"
          loading={cancel.isPending}
          onClick={async () => {
            try {
              await cancel.mutateAsync();
              toast.success("Work order cancelled");
            } catch {
              toast.error("Failed to cancel work order");
            }
          }}
        >
          Cancel
        </Button>
      )}

      {wo.resolutionNotes && (
        <Card>
          <p className="text-sm font-medium">Resolution Notes</p>
          <p className="text-sm text-muted-foreground">{wo.resolutionNotes}</p>
        </Card>
      )}
    </Stack>
  );
}
