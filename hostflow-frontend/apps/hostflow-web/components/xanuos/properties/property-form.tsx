"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import {
  propertyFormSchema,
  type PropertyFormValues,
} from "@hostflow/validation";
import type { PropertyType } from "@hostflow/types";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateProperty } from "@hostflow/api-client/src/hooks/use-properties";
import { apiUpload } from "@hostflow/api-client/src/http-client";

// A suggested default only -- the owner is the only one who actually knows
// whether e.g. an OFFICE listing is a day-rate conference space or a
// monthly-rent workspace lease, so this never locks the field, just
// pre-selects a sensible starting point when propertyType changes.
const SUGGESTED_RENTAL_MODEL: Record<PropertyType, "NIGHTLY" | "MONTHLY"> = {
  RESIDENTIAL: "MONTHLY",
  HOTEL: "NIGHTLY",
  VACATION_RENTAL: "NIGHTLY",
  OFFICE: "NIGHTLY",
  RETAIL_MALL: "MONTHLY",
  MIXED_USE: "NIGHTLY",
};

export function PropertyForm() {
  const router = useRouter();
  const createProperty = useCreateProperty();
  const [photo, setPhoto] = useState<File | null>(null);

  const form = useForm<PropertyFormValues>({
    resolver: zodResolver(propertyFormSchema),
    defaultValues: {
      name: "",
      propertyType: "RESIDENTIAL",
      rentalModel: "MONTHLY",
      addressLine: "",
      city: "",
      country: "",
    },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      const created = await createProperty.mutateAsync(values);
      if (photo) {
        // Best-effort: the property itself is already created at this point,
        // so a photo upload failure shouldn't block navigation — the owner
        // can always add one later from the property's own page.
        try {
          const formData = new FormData();
          formData.append("file", photo);
          formData.append("documentType", "PHOTO");
          await apiUpload(`/properties/${created.id}/documents`, formData);
        } catch {
          toast.error("Property created, but the photo failed to upload — you can add it from the property page");
        }
      }
      toast.success("Property created");
      router.push(`/xanuos/properties/${created.id}`);
    } catch {
      toast.error("Failed to create property");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Input
          label="Name"
          {...form.register("name")}
          error={form.formState.errors.name?.message}
        />
        <Select
          label="Property Type"
          {...form.register("propertyType", {
            onChange: (e) => {
              const type = e.target.value as PropertyType;
              form.setValue("rentalModel", SUGGESTED_RENTAL_MODEL[type]);
            },
          })}
          options={[
            { value: "RESIDENTIAL", label: "Residential" },
            { value: "HOTEL", label: "Hotel" },
            { value: "VACATION_RENTAL", label: "Vacation Rental" },
            { value: "OFFICE", label: "Office" },
            { value: "RETAIL_MALL", label: "Retail / Mall" },
            { value: "MIXED_USE", label: "Mixed Use" },
          ]}
        />
        <Select
          label="Rental Model"
          {...form.register("rentalModel")}
          options={[
            { value: "NIGHTLY", label: "Short-term (nightly / daily rate)" },
            { value: "MONTHLY", label: "Long-term (monthly rent)" },
          ]}
        />
        <p className="text-sm text-muted-foreground">
          Short-term properties (hotels, Airbnbs, event halls, day-rate offices) use
          check-in/check-out dates and guests can book instantly. Long-term properties
          (apartments, houses, bedsitters, leased offices) are rented by the month —
          guests send an inquiry instead of booking dates, and you follow up to arrange
          the lease.
        </p>
        <Input
          label="Address"
          {...form.register("addressLine")}
          error={form.formState.errors.addressLine?.message}
        />
        <Input
          label="City"
          {...form.register("city")}
          error={form.formState.errors.city?.message}
        />
        <Input
          label="Country"
          {...form.register("country")}
          error={form.formState.errors.country?.message}
        />
        <Input
          label="Cover Photo (optional)"
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={(e) => setPhoto(e.target.files?.[0] ?? null)}
        />
        <Button type="submit" loading={createProperty.isPending}>
          Create Property
        </Button>
      </Stack>
    </form>
  );
}
