"use client";

import { useState } from "react";
import { Card, Stack, Textarea, Button, Badge, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle } from "lucide-react";
import { useSendRentalInquiry } from "@hostflow/api-client/src/hooks/use-rental";
import { usePropertyOccupancy } from "@hostflow/api-client/src/hooks/use-public-properties";

// Optional support/engagement channel alongside ReserveRentalCard, NOT a
// gate in front of it -- a guest confident they want the unit can reserve
// it directly; this is for guests who have a question first (move-in
// timing, pet policy, etc.) or want to be first in line while it's
// occupied. See module-rental's Lease/RentalPortalController on the backend.
//
// "Available" here means "no active lease covers today" -- occupied
// properties still show the inquiry form (a prospective tenant may want to
// be first in line for when it frees up), just with occupancy status
// front-and-center instead of implying instant availability.
export function RentalInquiryCard({ propertyId }: { propertyId: string }) {
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
    <Card>
      <Stack gap="md">
        <p className="font-medium">Have a question?</p>
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
              {occupancy.occupied ? "Currently occupied" : "Available"}
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
            <Button variant="outline" onClick={onSend} loading={sendInquiry.isPending}>
              Send Message to Owner
            </Button>
          </>
        )}
      </Stack>
    </Card>
  );
}
