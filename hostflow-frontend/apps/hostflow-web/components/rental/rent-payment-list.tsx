"use client";

import { toast } from "sonner";
import { Stack, Badge, Button, Skeleton, EmptyState, Card } from "@hostflow/ui";
import {
  useRentPayments,
  useMarkRentPaid,
  useWaiveRentPayment,
} from "@hostflow/api-client/src/hooks/use-rental";

export function RentPaymentList({ leaseId }: { leaseId: string }) {
  const { data, isLoading, isError } = useRentPayments(leaseId);
  const markPaid = useMarkRentPaid(leaseId);
  const waive = useWaiveRentPayment(leaseId);

  if (isLoading) return <Skeleton className="h-48 w-full" />;
  if (isError) {
    return <EmptyState title="Couldn't load rent schedule" description="Try refreshing." />;
  }
  if (!data || data.length === 0) {
    return <EmptyState title="No rent schedule yet" description="Activate the lease to generate one." />;
  }

  return (
    <Stack gap="sm">
      {data.map((payment) => (
        <Card key={payment.id}>
          <Stack direction="row" justify="between" align="center">
            <Stack direction="row" gap="sm" align="center">
              <Badge>{payment.status}</Badge>
              <span className="text-sm">Due {payment.dueDate}</span>
              <span className="text-sm font-medium">${payment.amount}</span>
              {payment.paidDate && (
                <span className="text-xs text-muted-foreground">Paid {payment.paidDate}</span>
              )}
            </Stack>
            {payment.status === "DUE" || payment.status === "LATE" ? (
              <Stack direction="row" gap="sm">
                <Button
                  size="sm"
                  loading={markPaid.isPending}
                  onClick={async () => {
                    try {
                      await markPaid.mutateAsync(payment.id);
                      toast.success("Marked as paid");
                    } catch {
                      toast.error("Failed to mark as paid");
                    }
                  }}
                >
                  Mark Paid
                </Button>
                <Button
                  size="sm"
                  variant="outline"
                  loading={waive.isPending}
                  onClick={async () => {
                    try {
                      await waive.mutateAsync(payment.id);
                      toast.success("Payment waived");
                    } catch {
                      toast.error("Failed to waive payment");
                    }
                  }}
                >
                  Waive
                </Button>
              </Stack>
            ) : null}
          </Stack>
        </Card>
      ))}
    </Stack>
  );
}
