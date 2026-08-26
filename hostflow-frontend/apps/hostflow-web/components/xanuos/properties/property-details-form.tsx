"use client";

import { useState } from "react";
import { Button, Input, Textarea, Stack, toast } from "@hostflow/ui";
import { useUpdatePropertyDetails } from "@hostflow/api-client/src/hooks/use-properties";
import type { PropertyResponse } from "@hostflow/types";

// A property can be created, have photos uploaded, and be published without
// ever being priced or placed on a map — none of the create/publish steps
// touch description/basePrice/latitude/longitude. This form is what makes a
// published listing actually presentable on NazilCo: without a price it
// shows "Price on request", and without coordinates it's invisible on the
// discover map and has no "get directions" support.
export function PropertyDetailsForm({ property }: { property: PropertyResponse }) {
  const updateDetails = useUpdatePropertyDetails(property.id);
  const [description, setDescription] = useState(property.description ?? "");
  const [basePrice, setBasePrice] = useState(property.basePrice ?? "");
  const [latitude, setLatitude] = useState(property.latitude?.toString() ?? "");
  const [longitude, setLongitude] = useState(property.longitude?.toString() ?? "");
  const [totalUnits, setTotalUnits] = useState(String(property.totalUnits));
  const [occupiedUnits, setOccupiedUnits] = useState(String(property.occupiedUnits));

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await updateDetails.mutateAsync({
        description: description || undefined,
        basePrice: basePrice || undefined,
        latitude: latitude !== "" ? Number(latitude) : undefined,
        longitude: longitude !== "" ? Number(longitude) : undefined,
        totalUnits: totalUnits !== "" ? Number(totalUnits) : undefined,
        occupiedUnits: occupiedUnits !== "" ? Number(occupiedUnits) : undefined,
      });
      toast.success("Listing details saved");
    } catch {
      toast.error("Couldn't save listing details");
    }
  };

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Textarea
          label="Description"
          placeholder="What makes this stay worth booking?"
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <Input
          label={property.rentalModel === "MONTHLY" ? "Monthly rent (KSh)" : "Nightly rate (KSh)"}
          type="number"
          min={0}
          step="0.01"
          placeholder="e.g. 18500"
          value={basePrice}
          onChange={(e) => setBasePrice(e.target.value)}
        />
        <Stack direction="row" gap="md">
          <Input
            label="Latitude"
            type="number"
            step="any"
            placeholder="-1.2921"
            value={latitude}
            onChange={(e) => setLatitude(e.target.value)}
          />
          <Input
            label="Longitude"
            type="number"
            step="any"
            placeholder="36.8219"
            value={longitude}
            onChange={(e) => setLongitude(e.target.value)}
          />
        </Stack>
        <p className="text-xs text-muted-foreground">
          Right-click the location on Google Maps and choose the coordinates to copy them here.
          Without these, the property won&rsquo;t appear on NazilCo&rsquo;s map or support
          directions.
        </p>
        <Stack direction="row" gap="md">
          <Input
            label="Total units"
            type="number"
            min={1}
            step="1"
            value={totalUnits}
            onChange={(e) => setTotalUnits(e.target.value)}
          />
          <Input
            label="Occupied units"
            type="number"
            min={0}
            step="1"
            value={occupiedUnits}
            onChange={(e) => setOccupiedUnits(e.target.value)}
          />
        </Stack>
        <p className="text-xs text-muted-foreground">
          For multi-unit properties (e.g. an apartment building) -- you report how many of the
          total units are currently occupied. Not derived automatically from bookings or leases.
        </p>
        <Button type="submit" loading={updateDetails.isPending} className="w-fit">
          Save listing details
        </Button>
      </Stack>
    </form>
  );
}
