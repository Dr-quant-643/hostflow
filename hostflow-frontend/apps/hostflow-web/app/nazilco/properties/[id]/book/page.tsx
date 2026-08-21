"use client";

import { Suspense } from "react";
import { useParams, useSearchParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { motion } from "framer-motion";
import {
  guestBookingFormSchema,
  type GuestBookingFormValues,
} from "@hostflow/validation";
import {
  PageHeader,
  Stack,
  Card,
  Input,
  Button,
  Skeleton,
  EmptyState,
  toast,
} from "@hostflow/ui";
import { nightsBetween } from "@hostflow/api-client/src/hooks/use-public-booking";
import { usePublicProperty, useDemoCreateBooking } from "@/lib/demo-hooks";
import { formatKES } from "@/lib/currency";
import { BrandBackground } from "@/components/nazilco/layout/brand-background";
import { BookingSteps } from "@/components/nazilco/property/booking-steps";

export default function BookPropertyPage() {
  return (
    <Suspense fallback={null}>
      <BookPropertyPageContent />
    </Suspense>
  );
}

// useSearchParams() (used for the checkIn/checkOut query params) requires a
// Suspense boundary during static generation, or `next build` fails
// prerendering this route entirely.
function BookPropertyPageContent() {
  const { id } = useParams<{ id: string }>();
  const searchParams = useSearchParams();
  const router = useRouter();

  const { data: property, isLoading } = usePublicProperty(id);
  const createBooking = useDemoCreateBooking(id);

  const form = useForm<GuestBookingFormValues>({
    resolver: zodResolver(guestBookingFormSchema),
    defaultValues: {
      checkIn: searchParams.get("checkIn") ?? "",
      checkOut: searchParams.get("checkOut") ?? "",
    },
  });

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (!property) return <EmptyState title="Property not found" />;

  const values = form.watch();
  const nights = nightsBetween(values.checkIn, values.checkOut);
  const totalPrice =
    property.basePrice && nights > 0
      ? (Number(property.basePrice) * nights).toFixed(2)
      : null;

  const onSubmit = form.handleSubmit(async (formValues) => {
    if (!totalPrice) {
      toast.error("Select valid check-in/check-out dates first");
      return;
    }
    try {
      const booking = await createBooking.mutateAsync({ ...formValues, totalPrice });
      router.push(`/checkout/${booking.id}`);
    } catch {
      toast.error("Couldn't create your booking. Please try again.");
    }
  });

  return (
    <>
      <BrandBackground />
      <Stack gap="lg" className="mx-auto max-w-2xl p-6">
        <PageHeader title={`Book ${property.name}`} description={property.city} />
        <BookingSteps current={0} />
      <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
        <Card className="overflow-hidden">
          {property.photos?.[0] && (
            <div className="-m-6 mb-6 h-48 w-[calc(100%+3rem)] overflow-hidden">
              <motion.img
                src={property.photos[0]}
                alt=""
                className="h-full w-full object-cover"
                initial={{ scale: 1.05 }}
                animate={{ scale: 1 }}
                transition={{ duration: 0.6 }}
              />
            </div>
          )}
          <form onSubmit={onSubmit}>
            <Stack gap="md">
              <Input
                type="date"
                label="Check In"
                {...form.register("checkIn")}
                error={form.formState.errors.checkIn?.message}
              />
              <Input
                type="date"
                label="Check Out"
                {...form.register("checkOut")}
                error={form.formState.errors.checkOut?.message}
              />
              {totalPrice && (
                <motion.p
                  key={totalPrice}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="rounded-lg bg-muted p-3 text-sm text-muted-foreground"
                >
                  {nights} night{nights === 1 ? "" : "s"} × {formatKES(property.basePrice)} ={" "}
                  <span className="font-medium text-foreground">{formatKES(totalPrice)}</span>
                </motion.p>
              )}
              <Button type="submit" loading={createBooking.isPending} disabled={!totalPrice}>
                Continue to Checkout
              </Button>
            </Stack>
          </form>
        </Card>
      </motion.div>
      </Stack>
    </>
  );
}
