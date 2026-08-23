"use client";

import { useParams, useRouter } from "next/navigation";
import { motion } from "framer-motion";
import {
  PageHeader,
  Stack,
  Card,
  Skeleton,
  EmptyState,
  Button,
  Badge,
  toast,
} from "@hostflow/ui";
import { CheckCircle2, ShieldCheck, MapPin } from "lucide-react";
import { usePublicProperty, usePropertyPhotos } from "@hostflow/api-client/src/hooks/use-public-properties";
import { useBookingForCheckout, useConfirmCheckout } from "@hostflow/api-client/src/hooks/use-checkout";
import { formatKES } from "@/lib/currency";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";
import { BookingSteps } from "@/components/nazilco/property/booking-steps";

export default function CheckoutPage() {
  const { bookingId } = useParams<{ bookingId: string }>();
  const router = useRouter();
  const {
    data: booking,
    isLoading,
    isError,
  } = useBookingForCheckout(bookingId);
  const { data: property } = usePublicProperty(booking?.propertyId ?? "");
  const { data: photoUrls } = usePropertyPhotos(booking?.propertyId ?? "");
  const confirmCheckout = useConfirmCheckout(bookingId);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !booking) return <EmptyState title="Booking not found" />;

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-lg p-6">
        <PageHeader
          title="Checkout"
          description={property ? property.name : "Loading property…"}
        />
        <BookingSteps current={booking.status === "PENDING" ? 1 : 2} />
        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
          <Card className="overflow-hidden p-6">
            {photoUrls?.[0] && (
              <div className="-mx-6 -mt-6 mb-6 h-44 overflow-hidden">
                <img src={photoUrls[0]} alt="" className="h-full w-full object-cover" />
              </div>
            )}
            <Stack gap="md">
              {property?.city && (
                <span className="flex items-center gap-1 text-xs text-muted-foreground">
                  <MapPin className="h-3.5 w-3.5" /> {property.city}, {property.country}
                </span>
              )}
              <Stack direction="row" gap="sm" align="center">
                <span className="text-sm">{booking.checkIn}</span>
                <span className="text-muted-foreground">→</span>
                <span className="text-sm">{booking.checkOut}</span>
                <Badge variant={booking.status === "PENDING" ? "warning" : "success"}>{booking.status}</Badge>
              </Stack>

              <div className="rounded-xl border border-border/60 bg-muted/40 p-3">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Total</span>
                  <span className="text-lg font-semibold">{formatKES(booking.totalPrice)}</span>
                </div>
              </div>

              {booking.status === "PENDING" ? (
                <>
                  <Button
                    loading={confirmCheckout.isPending}
                    onClick={async () => {
                      try {
                        await confirmCheckout.mutateAsync();
                        toast.success("Booking confirmed");
                        router.push("/nazilco/guest-portal");
                      } catch {
                        toast.error("Couldn't confirm your booking");
                      }
                    }}
                  >
                    Confirm Booking
                  </Button>
                  <p className="flex items-center justify-center gap-1.5 text-xs text-muted-foreground">
                    <ShieldCheck className="h-3.5 w-3.5" />
                    Free cancellation up to 48 hours before check-in
                  </p>
                </>
              ) : (
                <motion.p
                  initial={{ opacity: 0, scale: 0.96 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="flex items-center gap-1.5 rounded-lg bg-success/10 p-3 text-sm text-success"
                >
                  <CheckCircle2 className="h-4 w-4" />
                  This booking is already {booking.status.toLowerCase()}.
                </motion.p>
              )}
            </Stack>
          </Card>
        </motion.div>
      </Stack>
    </>
  );
}
