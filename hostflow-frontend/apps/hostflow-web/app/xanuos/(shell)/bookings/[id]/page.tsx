"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, Textarea, toast } from "@hostflow/ui";
import {
  useBooking,
  useConfirmBooking,
  useCancelBooking,
  useDeclineBooking,
} from "@hostflow/api-client/src/hooks/use-bookings";

export default function BookingDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: booking, isLoading, isError } = useBooking(id);
  const confirm = useConfirmBooking(id);
  const cancel = useCancelBooking(id);
  const decline = useDeclineBooking(id);
  const [declining, setDeclining] = useState(false);
  const [reason, setReason] = useState("");

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !booking) return <EmptyState title="Booking not found" />;

  return (
    <Stack gap="md">
      <PageHeader
        title={`Booking ${booking.id.slice(0, 8)}`}
        description={`${booking.checkIn} → ${booking.checkOut} · $${booking.totalPrice}`}
        actions={<Badge>{booking.status}</Badge>}
      />
      {booking.status === "DECLINED" && booking.declineReason && (
        <p className="rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Declined: {booking.declineReason}
        </p>
      )}
      <Stack direction="row" gap="sm">
        {booking.status === "PENDING" && !declining && (
          <>
            <Button
              loading={confirm.isPending}
              onClick={async () => {
                try {
                  await confirm.mutateAsync();
                  toast.success("Booking confirmed");
                } catch {
                  toast.error("Failed to confirm");
                }
              }}
            >
              Confirm
            </Button>
            <Button variant="outline" onClick={() => setDeclining(true)}>
              Decline
            </Button>
          </>
        )}
        {booking.status !== "CANCELLED" && booking.status !== "CHECKED_OUT" && booking.status !== "DECLINED" && (
          <Button
            variant="destructive"
            loading={cancel.isPending}
            onClick={async () => {
              try {
                await cancel.mutateAsync();
                toast.success("Booking cancelled");
              } catch {
                toast.error("Failed to cancel");
              }
            }}
          >
            Cancel Booking
          </Button>
        )}
      </Stack>
      {declining && (
        <Stack gap="sm" className="max-w-md">
          <Textarea
            label="Reason for declining"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Let the guest know why this booking isn't going ahead"
          />
          <Stack direction="row" gap="sm">
            <Button
              variant="destructive"
              disabled={!reason.trim()}
              loading={decline.isPending}
              onClick={async () => {
                try {
                  await decline.mutateAsync(reason);
                  toast.success("Booking declined");
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
    </Stack>
  );
}
