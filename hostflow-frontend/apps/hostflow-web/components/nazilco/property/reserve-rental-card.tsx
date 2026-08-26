"use client";

import { useState } from "react";
import Link from "next/link";
import { Card, Stack, Input, Select, Button, toast } from "@hostflow/ui";
import { CheckCircle2, XCircle, Clock, Wrench } from "lucide-react";
import { useReserveRental, useMyLeases } from "@hostflow/api-client/src/hooks/use-rental";
import type { MyLeaseRow } from "@hostflow/types";
import { formatKES } from "@/lib/currency";

const DURATION_OPTIONS = [6, 12, 24];

// The direct-booking path for MONTHLY properties, alongside (not gated
// behind) RentalInquiryCard -- some guests want to just reserve a unit
// without waiting on an owner's reply to an inquiry. Creates a DRAFT Lease
// immediately; the owner is notified and must approve or decline it before
// it's ACTIVE (see LeaseController.decline/RentalReservationOrchestrator).
// Shows the guest's own real, persisted lease status instead of a
// page-local "confirmed" flash that reset on navigation.
export function ReserveRentalCard({ propertyId, basePrice }: { propertyId: string; basePrice: string | null }) {
  const [moveInDate, setMoveInDate] = useState("");
  const [months, setMonths] = useState(12);
  const reserve = useReserveRental(propertyId);

  const { data: myLeases } = useMyLeases();
  const existing = myLeases?.find((l) => l.propertyId === propertyId && l.status !== "TERMINATED" && l.status !== "EXPIRED");

  const onReserve = async () => {
    if (!moveInDate) return;
    try {
      await reserve.mutateAsync({ moveInDate, months });
      toast.success("Reservation request sent — awaiting the owner's approval");
    } catch {
      toast.error("Couldn't submit your reservation. Please try again.");
    }
  };

  if (existing) {
    return <ReservationStatusCard lease={existing} />;
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

function ReservationStatusCard({ lease }: { lease: MyLeaseRow }) {
  if (lease.status === "DRAFT") {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-amber-600">
            <Clock className="h-5 w-5" />
            <p className="font-medium">Awaiting approval</p>
          </div>
          <p className="text-sm text-muted-foreground">
            {lease.startDate} → {lease.endDate} · {formatKES(lease.monthlyRent)}/mo
          </p>
          <p className="text-xs text-muted-foreground">The owner hasn&apos;t responded yet.</p>
        </Stack>
      </Card>
    );
  }
  if (lease.status === "DECLINED") {
    return (
      <Card className="shadow-lg">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-destructive">
            <XCircle className="h-5 w-5" />
            <p className="font-medium">Declined</p>
          </div>
          <p className="text-sm text-muted-foreground">
            {lease.declineReason ?? "The owner declined this reservation."}
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
          <p className="font-medium">Reservation confirmed</p>
        </div>
        <p className="text-sm text-muted-foreground">
          Your lease runs from {lease.startDate} to {lease.endDate}.
        </p>
        <Link
          href={`/nazilco/support?propertyId=${lease.propertyId}`}
          className="flex items-center gap-1.5 text-xs text-muted-foreground underline hover:text-foreground"
        >
          <Wrench className="h-3 w-3" />
          Report a maintenance issue
        </Link>
      </Stack>
    </Card>
  );
}
