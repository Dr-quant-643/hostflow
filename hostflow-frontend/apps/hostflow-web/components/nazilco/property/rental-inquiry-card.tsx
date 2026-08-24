"use client";

import { useState } from "react";
import { Card, Stack, Textarea, Button, Badge, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle } from "lucide-react";
import { useSendRentalInquiry } from "@hostflow/api-client/src/hooks/use-rental";
import { usePropertyOccupancy } from "@hostflow/api-client/src/hooks/use-public-properties";
import { formatKES } from "@/lib/currency";

// MONTHLY-classified properties (apartments, houses, bedsitters, leased
// offices) have no self-service booking flow -- there's no "check-out date"
// for a lease. A guest sends an inquiry instead; the owner follows up and
// arranges the lease through XanuOS's existing staff-facing flow. See
// module-rental's Lease/RentalPortalController on the backend.
//
// "Available" here means "no active lease covers today" -- occupied
// properties still show the inquiry form (a prospective tenant may want to
// be first in line for when it frees up), just with occupancy status
// front-and-center instead of implying instant availability.
export function RentalInquiryCard({ propertyId, basePrice }: { propertyId: string; basePrice: string | null }) {
  const [message, setMessage] = useState("");
  const [sent, setSent] = useState(false);
  const sendInquiry = useSendRentalInquiry(propertyId);
  const { data: occupancy } = usePropertyOccupancy(propertyId, true);

  const onSend = async () => {
    try {
      await sendInquiry.mutateAsync(message || undefined);
      setSent(true);
      toast.success("Your inquiry has been sent to the owner");
    } catch {
      toast.error("Couldn't send your inquiry. Please try again.");
    }
  };

  return (
    <Card className="shadow-lg">
      <Stack gap="md">
        {basePrice && (
          <p className="text-xl font-semibold">
            {formatKES(basePrice)}
            <span className="text-sm font-normal text-muted-foreground"> / month</span>
          </p>
        )}
        {occupancy && (
          <Stack gap="sm">
            <Badge
              variant={occupancy.occupied ? "outline" : "success"}
              className="flex w-fit items-center gap-1"
            >
              {occupancy.occupied ? (
                <XCircle className="h-3.5 w-3.5" />
              ) : (
                <CheckCircle2 className="h-3.5 w-3.5" />
              )}
              {occupancy.occupied ? "Currently occupied" : "Available — inquire to rent"}
            </Badge>
            <p className="text-xs text-muted-foreground">
              {occupancy.occupancyRatePercent}% occupied historically
            </p>
          </Stack>
        )}
        {sent ? (
          <p className="text-sm text-muted-foreground">
            Thanks — the owner has been notified and will follow up with you directly.
          </p>
        ) : (
          <>
            <Textarea
              placeholder="Optional message to the owner (e.g. when you'd like to move in)"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              rows={3}
            />
            <Button onClick={onSend} loading={sendInquiry.isPending}>
              Inquire About This Rental
            </Button>
          </>
        )}
      </Stack>
    </Card>
  );
}
