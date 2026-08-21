"use client";

import { useParams, useRouter } from "next/navigation";
import { motion } from "framer-motion";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Card, Button, toast } from "@hostflow/ui";
import { MapPin } from "lucide-react";
import {
  usePublicProperty,
  useDemoMyBooking,
  useDemoCancelBooking,
} from "@/lib/demo-hooks";
import { formatKES } from "@/lib/currency";
import { LeaveReviewForm } from "@/components/nazilco/guest-portal/leave-review-form";

export default function TripDetailPage() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const { data: booking, isLoading, isError } = useDemoMyBooking(id);
  const { data: property } = usePublicProperty(booking?.propertyId ?? "");
  const cancel = useDemoCancelBooking(id);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !booking) return <EmptyState title="Trip not found" />;

  const cancellable = booking.status === "PENDING" || booking.status === "CONFIRMED";

  return (
    <Stack gap="lg" className="mx-auto max-w-2xl p-6">
      <PageHeader
        title={property?.name ?? "Trip"}
        description={`${booking.checkIn} → ${booking.checkOut}`}
        actions={<Badge>{booking.status}</Badge>}
      />

      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
        <Card className="overflow-hidden p-6">
          {property?.photos?.[0] && (
            <div className="-mx-6 -mt-6 mb-6 h-48">
              <img src={property.photos[0]} alt="" className="h-full w-full object-cover" />
            </div>
          )}
          <Stack gap="sm">
            {property && (
              <p className="flex items-center gap-1.5 text-sm text-muted-foreground">
                <MapPin className="h-4 w-4" />
                {property.city}, {property.country}
              </p>
            )}
            <p className="font-medium">{formatKES(booking.totalPrice)} total</p>
            {cancellable && (
              <Button
                variant="destructive"
                loading={cancel.isPending}
                onClick={async () => {
                  try {
                    await cancel.mutateAsync();
                    toast.success("Booking cancelled");
                    router.push("/guest-portal");
                  } catch {
                    toast.error("Couldn't cancel this booking");
                  }
                }}
              >
                Cancel Booking
              </Button>
            )}
          </Stack>
        </Card>
      </motion.div>

      {booking.status === "CHECKED_OUT" && (
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: 0.1 }}>
          <Card className="p-6">
            <Stack gap="md">
              <h3 className="font-medium">Leave a Review</h3>
              <LeaveReviewForm bookingId={booking.id} />
            </Stack>
          </Card>
        </motion.div>
      )}
    </Stack>
  );
}
