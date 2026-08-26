"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, Textarea, toast } from "@hostflow/ui";
import { useLease, useActivateLease, useTerminateLease, useDeclineLease } from "@hostflow/api-client/src/hooks/use-rental";
import { RentPaymentList } from "@/components/xanuos/rental/rent-payment-list";

export default function LeaseDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: lease, isLoading, isError } = useLease(id);
  const activate = useActivateLease(id);
  const terminate = useTerminateLease(id);
  const decline = useDeclineLease(id);
  const [declining, setDeclining] = useState(false);
  const [reason, setReason] = useState("");

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
            {lease.status === "DRAFT" && !declining && (
              <>
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
                  Approve
                </Button>
                <Button variant="outline" onClick={() => setDeclining(true)}>
                  Decline
                </Button>
              </>
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
      {lease.status === "DECLINED" && lease.declineReason && (
        <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Declined: {lease.declineReason}
        </p>
      )}
      {declining && (
        <Stack gap="sm" className="max-w-md">
          <Textarea
            label="Reason for declining"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Let the tenant know why this reservation isn't going ahead"
          />
          <Stack direction="row" gap="sm">
            <Button
              variant="destructive"
              disabled={!reason.trim()}
              loading={decline.isPending}
              onClick={async () => {
                try {
                  await decline.mutateAsync(reason);
                  toast.success("Lease declined");
                  setDeclining(false);
                } catch {
                  toast.error("Failed to decline");
                }
              }}
            >
              Confirm Decline
            </Button>
            <Button variant="outline" onClick={() => setDeclining(false)}>
              Cancel
            </Button>
          </Stack>
        </Stack>
      )}
      <RentPaymentList leaseId={lease.id} />
    </Stack>
  );
}
