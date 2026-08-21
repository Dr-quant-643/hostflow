"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Button, Input, Stack, Badge } from "@hostflow/ui";
import { CheckCircle2, XCircle } from "lucide-react";
import { useCheckAvailability } from "@hostflow/api-client/src/hooks/use-public-properties";

// Real backend only exposes GET /bookings/public/availability?propertyId&
// checkIn&checkOut -> boolean (PublicAvailabilityQueries.isAvailable) — there
// is no day-by-day calendar endpoint, so this is a range-check form rather
// than a month grid.
export function AvailabilityCalendar({
  propertyId,
  onSelectRange,
}: {
  propertyId: string;
  onSelectRange: (checkIn: string, checkOut: string) => void;
}) {
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [checked, setChecked] = useState<{ checkIn: string; checkOut: string } | null>(null);

  const { data: available, isFetching } = useCheckAvailability(
    propertyId,
    checked?.checkIn ?? "",
    checked?.checkOut ?? "",
  );

  const canCheck = !!checkIn && !!checkOut && checkOut > checkIn;

  return (
    <Stack gap="sm">
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
        {checked && !isFetching && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.25 }}
          >
            <Stack gap="sm">
              <Badge variant={available ? "success" : "outline"} className="flex w-fit items-center gap-1">
                {available ? (
                  <CheckCircle2 className="h-3.5 w-3.5" />
                ) : (
                  <XCircle className="h-3.5 w-3.5" />
                )}
                {available ? "Available" : "Not available for these dates"}
              </Badge>
              {available && (
                <Button variant="outline" onClick={() => onSelectRange(checked.checkIn, checked.checkOut)}>
                  Book These Dates
                </Button>
              )}
            </Stack>
          </motion.div>
        )}
      </AnimatePresence>
    </Stack>
  );
}
