"use client";

import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, toast } from "@hostflow/ui";
import { useLease, useActivateLease, useTerminateLease } from "@hostflow/api-client/src/hooks/use-rental";
import { RentPaymentList } from "@/components/rental/rent-payment-list";

export default function LeaseDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: lease, isLoading, isError } = useLease(id);
  const activate = useActivateLease(id);
  const terminate = useTerminateLease(id);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !lease) return <EmptyState title="Lease not found" />;

  return (
    <Stack gap="lg">
      <PageHeader
        title={`Lease ${lease.id.slice(0, 8)}`}
        description={`${lease.startDate} → ${lease.endDate} · $${lease.monthlyRent}/mo`}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge>{lease.status}</Badge>
            {lease.status === "DRAFT" && (
              <Button
                loading={activate.isPending}
                onClick={async () => {
                  try {
                    await activate.mutateAsync();
                    toast.success("Lease activated — rent schedule generated");
                  } catch {
                    toast.error("Failed to activate lease");
                  }
                }}
              >
                Activate
              </Button>
            )}
            {lease.status === "ACTIVE" && (
              <Button
                variant="destructive"
                loading={terminate.isPending}
                onClick={async () => {
                  try {
                    await terminate.mutateAsync();
                    toast.success("Lease terminated");
                  } catch {
                    toast.error("Failed to terminate lease");
                  }
                }}
              >
                Terminate
              </Button>
            )}
          </Stack>
        }
      />
      <RentPaymentList leaseId={lease.id} />
    </Stack>
  );
}
