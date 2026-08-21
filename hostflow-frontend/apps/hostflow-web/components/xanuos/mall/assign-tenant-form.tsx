"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  assignRetailTenantFormSchema,
  type AssignRetailTenantFormValues,
} from "@hostflow/validation";
import { Button, Input, Select, Stack, Card, toast } from "@hostflow/ui";
import { useAssignRetailTenant } from "@hostflow/api-client/src/hooks/use-mall";
import type { RetailUnitResponse } from "@hostflow/types";

export function AssignTenantForm({
  propertyId,
  units,
}: {
  propertyId: string;
  units: RetailUnitResponse[];
}) {
  const assignTenant = useAssignRetailTenant(propertyId);
  const vacantUnits = units.filter((u) => u.status === "VACANT");

  const form = useForm<AssignRetailTenantFormValues>({
    resolver: zodResolver(assignRetailTenantFormSchema),
    defaultValues: {
      retailUnitId: vacantUnits[0]?.id ?? "",
      businessName: "",
      contactEmail: "",
      contactPhone: "",
      monthlyRent: "0.00",
      revenueSharePercent: "",
    },
  });

  if (vacantUnits.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">No vacant units available to assign.</p>
    );
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await assignTenant.mutateAsync(values);
      toast.success("Tenant assigned");
      form.reset({
        retailUnitId: vacantUnits[0]?.id ?? "",
        businessName: "",
        contactEmail: "",
        contactPhone: "",
        monthlyRent: "0.00",
        revenueSharePercent: "",
      });
    } catch {
      toast.error("Failed to assign tenant");
    }
  });

  return (
    <Card>
      <Stack gap="md">
        <h3 className="font-medium">Assign Retail Tenant</h3>
        <form onSubmit={onSubmit}>
          <Stack direction="row" gap="sm" align="end">
            <Select
              label="Unit"
              {...form.register("retailUnitId")}
              options={vacantUnits.map((u) => ({ value: u.id, label: u.unitNumber }))}
            />
            <Input
              label="Business Name"
              {...form.register("businessName")}
              error={form.formState.errors.businessName?.message}
            />
            <Input label="Monthly Rent" {...form.register("monthlyRent")} />
            <Input label="Revenue Share %" {...form.register("revenueSharePercent")} />
            <Button type="submit" loading={assignTenant.isPending}>
              Assign
            </Button>
          </Stack>
        </form>
      </Stack>
    </Card>
  );
}
