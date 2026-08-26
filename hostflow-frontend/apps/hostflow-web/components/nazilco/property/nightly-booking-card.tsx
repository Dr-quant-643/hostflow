"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Card, Stack, Input, Button, Badge, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle } from "lucide-react";
import { useCheckAvailability } from "@hostflow/api-client/src/hooks/use-public-properties";
import { nightsBetween, useCreateAndConfirmGuestBooking } from "@hostflow/api-client/src/hooks/use-public-booking";
import { formatKES } from "@/lib/currency";

// Consolidates what used to be four separate steps across two pages (check
// availability -> navigate to /book -> submit -> navigate to /checkout to
// confirm) into one inline flow entirely on the property page. Confirming is
// a plain status flip with no payment/processor step in between, so there
// was no real step for a separate /checkout page to justify.
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
  const [confirmed, setConfirmed] = useState<{ checkIn: string; checkOut: string; totalPrice: string } | null>(null);

  const { data: availability, isFetching } = useCheckAvailability(
    propertyId,
    checked?.checkIn ?? "",
    checked?.checkOut ?? "",
  );
  const bookAndConfirm = useCreateAndConfirmGuestBooking(propertyId);

  const canCheck = !!checkIn && !!checkOut && checkOut > checkIn;
  const nights = checked ? nightsBetween(checked.checkIn, checked.checkOut) : 0;
  const totalPrice = basePrice && nights > 0 ? (Number(basePrice) * nights).toFixed(2) : null;

  const onBookNow = async () => {
    if (!checked || !totalPrice) return;
    try {
      await bookAndConfirm.mutateAsync({
        checkIn: checked.checkIn,
        checkOut: checked.checkOut,
        totalPrice,
      });
      setConfirmed({ checkIn: checked.checkIn, checkOut: checked.checkOut, totalPrice });
      toast.success("Booking confirmed");
    } catch {
      toast.error("Couldn't complete your booking. Please try again.");
    }
  };

  if (confirmed) {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-emerald-600">
            <CheckCircle2 className="h-5 w-5" />
            <p className="font-medium">Booking confirmed</p>
          </div>
          <p className="text-sm text-muted-foreground">
            {confirmed.checkIn} → {confirmed.checkOut} · {formatKES(confirmed.totalPrice)}
          </p>
        </Stack>
      </Card>
    );
  }

  return (
    <Card className="shadow-lg">
      <Stack gap="md">
        {basePrice ? (
          <p className="text-xl font-semibold">
            {formatKES(basePrice)}
            <span className="text-sm font-normal text-muted-foreground"> / night</span>
          </p>
        ) : (
          <p className="text-sm text-muted-foreground">Price on request</p>
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
                {availability.available && (
                  totalPrice ? (
                    <>
                      <p className="rounded-lg bg-muted p-3 text-sm text-muted-foreground">
                        {nights} night{nights === 1 ? "" : "s"} × {formatKES(basePrice)} ={" "}
                        <span className="font-medium text-foreground">{formatKES(totalPrice)}</span>
                      </p>
                      <Button onClick={onBookNow} loading={bookAndConfirm.isPending}>
                        Book Now
                      </Button>
                    </>
                  ) : (
                    <p className="text-sm text-destructive">
                      This property doesn&apos;t have a rate set yet, so it can&apos;t be booked online.
                      Please contact the owner directly.
                    </p>
                  )
                )}
              </Stack>
            </motion.div>
          )}
        </AnimatePresence>
      </Stack>
    </Card>
  );
}
