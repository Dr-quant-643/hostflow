"use client";

import { useState } from "react";
import { Card, Stack, Input, Textarea, Select, Button, toast } from "@hostflow/ui";
import { CheckCircle2 } from "lucide-react";
import { usePublicProperty } from "@hostflow/api-client/src/hooks/use-public-properties";
import { useSendMaintenanceRequest } from "@hostflow/api-client/src/hooks/use-maintenance";
import type { MaintenanceCategory } from "@hostflow/types";

const CATEGORY_OPTIONS: { value: MaintenanceCategory; label: string }[] = [
  { value: "PLUMBING", label: "Plumbing" },
  { value: "ELECTRICAL", label: "Electrical" },
  { value: "HVAC", label: "Heating / Cooling" },
  { value: "APPLIANCE", label: "Appliance" },
  { value: "STRUCTURAL", label: "Structural" },
  { value: "PEST_CONTROL", label: "Pest control" },
  { value: "CLEANING", label: "Cleaning" },
  { value: "OTHER", label: "Other" },
];

// Reached from a "Report a maintenance issue" link on a guest's own booking/
// reservation status card (NightlyBookingCard/ReserveRentalCard), which
// already knows the propertyId -- this is the one connection point between
// NazilCo (tenant) and XanuOS (owner) for maintenance; previously there was
// none, since module-maintenance's WorkOrderController is entirely
// PRODUCT_XANUOS.
export function MaintenanceRequestForm({ propertyId }: { propertyId: string }) {
  const { data: property } = usePublicProperty(propertyId);
  const sendRequest = useSendMaintenanceRequest();
  const [submitted, setSubmitted] = useState(false);
  const [category, setCategory] = useState<MaintenanceCategory>("OTHER");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  if (submitted) {
    return (
      <Card className="p-6">
        <Stack gap="sm">
          <div className="flex items-center gap-2 text-emerald-600">
            <CheckCircle2 className="h-5 w-5" />
            <p className="font-medium">Issue reported</p>
          </div>
          <p className="text-sm text-muted-foreground">The owner has been notified.</p>
        </Stack>
      </Card>
    );
  }

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;
    try {
      await sendRequest.mutateAsync({
        propertyId,
        category,
        title,
        description: description || undefined,
      });
      setSubmitted(true);
      toast.success("Maintenance issue reported");
    } catch {
      toast.error("Couldn't submit your report. Please try again.");
    }
  };

  return (
    <Card className="p-6">
      <form onSubmit={onSubmit}>
        <Stack gap="md">
          <div>
            <p className="font-medium">Report a maintenance issue</p>
            {property && <p className="text-sm text-muted-foreground">{property.name}</p>}
          </div>
          <Select
            label="Category"
            value={category}
            onChange={(e) => setCategory(e.target.value as MaintenanceCategory)}
            options={CATEGORY_OPTIONS}
          />
          <Input label="Title" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="e.g. Leaking kitchen tap" />
          <Textarea
            label="Description (optional)"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <Button type="submit" disabled={!title.trim()} loading={sendRequest.isPending}>
            Submit
          </Button>
        </Stack>
      </form>
    </Card>
  );
}
