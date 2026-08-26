"use client";

import { useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { Card, Stack, Input, Button, Badge, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle, Clock, Wrench } from "lucide-react";
import { useCheckAvailability } from "@hostflow/api-client/src/hooks/use-public-properties";
import { nightsBetween, useCreateGuestBooking } from "@hostflow/api-client/src/hooks/use-public-booking";
import { useMyBookings } from "@hostflow/api-client/src/hooks/use-guest-portal";
import type { BookingResponse } from "@hostflow/types";
import { formatKES } from "@/lib/currency";

// Consolidates what used to be three separate steps across two pages
// (AvailabilityCalendar on the property page -> navigate to /book -> submit
// there) into one inline flow -- there is no reason a NIGHTLY booking can't
// happen entirely on the property page. Booking itself now needs the
// property owner's approval (see BookingController.decline/PENDING status),
// so this shows the guest's own booking's real, persisted status instead of
// a page-local "confirmed" flash that reset on navigation.
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

  const { data: myBookings } = useMyBookings();
  const existing = myBookings?.find((b) => b.propertyId === propertyId && b.status !== "CANCELLED");

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
      await createBooking.mutateAsync({
        checkIn: checked.checkIn,
        checkOut: checked.checkOut,
        totalPrice,
      });
      toast.success("Booking request sent — awaiting the owner's approval");
    } catch {
      toast.error("Couldn't submit your booking. Please try again.");
    }
  };

  if (existing) {
    return <BookingStatusCard booking={existing} />;
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
                      <Button onClick={onBookNow} loading={createBooking.isPending}>
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

function ReportIssueLink({ propertyId }: { propertyId: string }) {
  return (
    <Link
      href={`/nazilco/support?propertyId=${propertyId}`}
      className="flex items-center gap-1.5 text-xs text-muted-foreground underline hover:text-foreground"
    >
      <Wrench className="h-3 w-3" />
      Report a maintenance issue
    </Link>
  );
}

function BookingStatusCard({ booking }: { booking: BookingResponse }) {
  if (booking.status === "PENDING") {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-amber-600">
            <Clock className="h-5 w-5" />
            <p className="font-medium">Awaiting approval</p>
          </div>
          <p className="text-sm text-muted-foreground">
            {booking.checkIn} → {booking.checkOut} · {formatKES(booking.totalPrice)}
          </p>
          <p className="text-xs text-muted-foreground">The owner hasn&apos;t responded yet.</p>
        </Stack>
      </Card>
    );
  }
  if (booking.status === "DECLINED") {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-destructive">
            <XCircle className="h-5 w-5" />
            <p className="font-medium">Declined</p>
          </div>
          <p className="text-sm text-muted-foreground">
            {booking.declineReason ?? "The owner declined this booking."}
          </p>
        </Stack>
      </Card>
    );
  }
  return (
    <Card className="shadow-lg">
      <Stack gap="sm">
        <div className="flex items-center gap-2 text-emerald-600">
          <CheckCircle2 className="h-5 w-5" />
          <p className="font-medium">Booking confirmed</p>
        </div>
        <p className="text-sm text-muted-foreground">
          {booking.checkIn} → {booking.checkOut} · {formatKES(booking.totalPrice)}
        </p>
        <ReportIssueLink propertyId={booking.propertyId} />
      </Stack>
    </Card>
  );
}
