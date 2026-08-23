"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import {
  propertyFormSchema,
  type PropertyFormValues,
} from "@hostflow/validation";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateProperty } from "@hostflow/api-client/src/hooks/use-properties";
import { apiUpload } from "@hostflow/api-client/src/http-client";

export function PropertyForm() {
  const router = useRouter();
  const createProperty = useCreateProperty();
  const [photo, setPhoto] = useState<File | null>(null);

  const form = useForm<PropertyFormValues>({
    resolver: zodResolver(propertyFormSchema),
    defaultValues: {
      name: "",
      propertyType: "RESIDENTIAL",
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
          {...form.register("propertyType")}
          options={[
            { value: "RESIDENTIAL", label: "Residential" },
            { value: "HOTEL", label: "Hotel" },
            { value: "VACATION_RENTAL", label: "Vacation Rental" },
            { value: "OFFICE", label: "Office" },
            { value: "RETAIL_MALL", label: "Retail / Mall" },
            { value: "MIXED_USE", label: "Mixed Use" },
          ]}
        />
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
