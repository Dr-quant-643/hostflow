"use client";

import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, toast } from "@hostflow/ui";
import {
  useBooking,
  useConfirmBooking,
  useCancelBooking,
} from "@hostflow/api-client/src/hooks/use-bookings";

export default function BookingDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: booking, isLoading, isError } = useBooking(id);
  const confirm = useConfirmBooking(id);
  const cancel = useCancelBooking(id);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !booking) return <EmptyState title="Booking not found" />;

  return (
    <Stack gap="md">
      <PageHeader
        title={`Booking ${booking.id.slice(0, 8)}`}
        description={`${booking.checkIn} → ${booking.checkOut} · $${booking.totalPrice}`}
        actions={<Badge>{booking.status}</Badge>}
      />
      <Stack direction="row" gap="sm">
        {booking.status === "PENDING" && (
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
        )}
        {booking.status !== "CANCELLED" && booking.status !== "CHECKED_OUT" && (
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
    </Stack>
  );
}
