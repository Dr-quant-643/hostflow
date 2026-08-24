"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Card, Stack, Input, Button, Badge, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle } from "lucide-react";
import { useCheckAvailability } from "@hostflow/api-client/src/hooks/use-public-properties";
import { nightsBetween, useCreateGuestBooking } from "@hostflow/api-client/src/hooks/use-public-booking";
import { formatKES } from "@/lib/currency";

// Consolidates what used to be three separate steps across two pages
// (AvailabilityCalendar on the property page -> navigate to /book -> submit
// there) into one inline flow. Splitting availability-checking from the
// actual booking form across pages was itself the source of the original
// stranding/redirect-loop bugs -- there is no reason a NIGHTLY booking can't
// happen entirely on the property page. The final "confirm" step still lives
// on its own /checkout/[bookingId] page (reviewing total + confirming is a
// materially different action from creating the booking).
export function NightlyBookingCard({
  propertyId,
  basePrice,
  initialCheckIn,
  initialCheckOut,
}: {
  propertyId: string;
  basePrice: string | null;
  initialCheckIn?: string;
  initialCheckOut?: string;
}) {
  const [checkIn, setCheckIn] = useState(initialCheckIn ?? "");
  const [checkOut, setCheckOut] = useState(initialCheckOut ?? "");
  const [checked, setChecked] = useState<{ checkIn: string; checkOut: string } | null>(
    initialCheckIn && initialCheckOut ? { checkIn: initialCheckIn, checkOut: initialCheckOut } : null,
  );

  const { data: availability, isFetching } = useCheckAvailability(
    propertyId,
    checked?.checkIn ?? "",
    checked?.checkOut ?? "",
  );
  const createBooking = useCreateGuestBooking(propertyId);

  const canCheck = !!checkIn && !!checkOut && checkOut > checkIn;
  const nights = checked ? nightsBetween(checked.checkIn, checked.checkOut) : 0;
  const totalPrice = basePrice && nights > 0 ? (Number(basePrice) * nights).toFixed(2) : null;

  const onBookNow = async () => {
    if (!checked || !totalPrice) return;
    try {
      const booking = await createBooking.mutateAsync({
        checkIn: checked.checkIn,
        checkOut: checked.checkOut,
        totalPrice,
      });
      // Full-page navigation: checkout is the confirm step, and this keeps
      // the same "no soft-nav across an auth boundary" discipline as the
      // rest of this flow, even though checkout doesn't itself redirect.
      window.location.href = `/nazilco/checkout/${booking.id}`;
    } catch {
      toast.error("Couldn't create your booking. Please try again.");
    }
  };

  return (
    <Card className="shadow-lg">
      <Stack gap="md">
        {basePrice && (
          <p className="text-xl font-semibold">
            {formatKES(basePrice)}
            <span className="text-sm font-normal text-muted-foreground"> / night</span>
          </p>
        )}
        <Input
          type="date"
          label="Check In"
          value={checkIn}
          onChange={(e) => {
            setCheckIn(e.target.value);
            setChecked(null);
          }}
        />
        <Input
          type="date"
          label="Check Out"
          value={checkOut}
          onChange={(e) => {
            setCheckOut(e.target.value);
            setChecked(null);
          }}
        />
        <Button
          disabled={!canCheck}
          loading={isFetching}
          onClick={() => setChecked({ checkIn, checkOut })}
        >
          Check Availability
        </Button>
        <AnimatePresence>
          {checked && !isFetching && availability && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.25 }}
            >
              <Stack gap="sm">
                <Badge
                  variant={availability.available ? "success" : "outline"}
                  className="flex w-fit items-center gap-1"
                >
                  {availability.available ? (
                    <CheckCircle2 className="h-3.5 w-3.5" />
                  ) : (
                    <XCircle className="h-3.5 w-3.5" />
                  )}
                  {availability.available ? "Available" : "Not available for these dates"}
                </Badge>
                {!availability.available && availability.availableFrom && (
                  <p className="text-sm text-muted-foreground">
                    This property is next available from{" "}
                    <span className="font-medium text-foreground">{availability.availableFrom}</span>.
                  </p>
                )}
                {availability.available && totalPrice && (
                  <>
                    <p className="rounded-lg bg-muted p-3 text-sm text-muted-foreground">
                      {nights} night{nights === 1 ? "" : "s"} × {formatKES(basePrice)} ={" "}
                      <span className="font-medium text-foreground">{formatKES(totalPrice)}</span>
                    </p>
                    <Button onClick={onBookNow} loading={createBooking.isPending}>
                      Book Now
                    </Button>
                  </>
                )}
              </Stack>
            </motion.div>
          )}
        </AnimatePresence>
      </Stack>
    </Card>
  );
}
