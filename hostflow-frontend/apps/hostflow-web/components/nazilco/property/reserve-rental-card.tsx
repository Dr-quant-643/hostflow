"use client";

import { useState } from "react";
import { Card, Stack, Input, Select, Button, toast } from "@hostflow/ui";
import { CheckCircle2 } from "lucide-react";
import { useReserveRental } from "@hostflow/api-client/src/hooks/use-rental";
import { formatKES } from "@/lib/currency";

const DURATION_OPTIONS = [6, 12, 24];

// The direct-booking path for MONTHLY properties, alongside (not gated
// behind) RentalInquiryCard -- some guests want to just reserve a unit
// without waiting on an owner's reply to an inquiry. Creates an ACTIVE Lease
// immediately; the owner is notified afterward rather than vetting first.
export function ReserveRentalCard({ propertyId, basePrice }: { propertyId: string; basePrice: string | null }) {
  const [moveInDate, setMoveInDate] = useState("");
  const [months, setMonths] = useState(12);
  const [confirmed, setConfirmed] = useState<{ startDate: string; endDate: string } | null>(null);
  const reserve = useReserveRental(propertyId);

  const onReserve = async () => {
    if (!moveInDate) return;
    try {
      const lease = await reserve.mutateAsync({ moveInDate, months });
      setConfirmed({ startDate: lease.startDate, endDate: lease.endDate });
      toast.success("Your reservation is confirmed");
    } catch {
      toast.error("Couldn't complete your reservation. Please try again.");
    }
  };

  if (confirmed) {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-emerald-600">
            <CheckCircle2 className="h-5 w-5" />
            <p className="font-medium">Reservation confirmed</p>
          </div>
          <p className="text-sm text-muted-foreground">
            Your lease runs from {confirmed.startDate} to {confirmed.endDate}. The owner has been notified.
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
            <span className="text-sm font-normal text-muted-foreground"> / month</span>
          </p>
        ) : (
          <p className="text-sm text-muted-foreground">Price on request</p>
        )}
        <Input type="date" label="Move-in date" value={moveInDate} onChange={(e) => setMoveInDate(e.target.value)} />
        <Select
          label="Lease length"
          value={String(months)}
          onChange={(e) => setMonths(Number(e.target.value))}
          options={DURATION_OPTIONS.map((m) => ({ value: String(m), label: `${m} months` }))}
        />
        <Button
          disabled={!moveInDate || !basePrice}
          loading={reserve.isPending}
          onClick={onReserve}
        >
          Reserve Now
        </Button>
        {!basePrice && (
          <p className="text-xs text-muted-foreground">
            This property doesn&apos;t have a rate set yet, so it can&apos;t be reserved online.
          </p>
        )}
      </Stack>
    </Card>
  );
}
