"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import {
  propertyFormSchema,
  type PropertyFormValues,
} from "@hostflow/validation";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateProperty } from "@hostflow/api-client/src/hooks/use-properties";

export function PropertyForm() {
  const router = useRouter();
  const createProperty = useCreateProperty();

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
      toast.success("Property created");
      router.push(`/properties/${created.id}`);
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
        <Button type="submit" loading={createProperty.isPending}>
          Create Property
        </Button>
      </Stack>
    </form>
  );
}
