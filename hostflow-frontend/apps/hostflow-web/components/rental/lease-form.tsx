"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { leaseFormSchema, type LeaseFormValues } from "@hostflow/validation";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateLease } from "@hostflow/api-client/src/hooks/use-rental";
import { useRentalTenants } from "@hostflow/api-client/src/hooks/use-rental";

export function LeaseForm({ propertyId }: { propertyId: string }) {
  const createLease = useCreateLease();
  const { data: tenants } = useRentalTenants();

  const form = useForm<LeaseFormValues>({
    resolver: zodResolver(leaseFormSchema),
    defaultValues: {
      propertyId,
      tenantIdRef: "",
      startDate: new Date().toISOString().slice(0, 10),
      endDate: "",
      monthlyRent: "0.00",
      securityDeposit: "",
    },
  });

  if (!tenants || tenants.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">
        Add a rental tenant first before creating a lease.
      </p>
    );
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createLease.mutateAsync({ ...values, propertyId });
      toast.success("Lease created (DRAFT)");
    } catch {
      toast.error("Failed to create lease");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Select
          label="Tenant"
          {...form.register("tenantIdRef")}
          options={tenants.map((t) => ({ value: t.id, label: t.fullName }))}
        />
        <Input label="Start Date" type="date" {...form.register("startDate")} />
        <Input label="End Date" type="date" {...form.register("endDate")} />
        <Input
          label="Monthly Rent"
          {...form.register("monthlyRent")}
          error={form.formState.errors.monthlyRent?.message}
        />
        <Input label="Security Deposit" {...form.register("securityDeposit")} />
        <Button type="submit" loading={createLease.isPending}>
          Create Lease
        </Button>
      </Stack>
    </form>
  );
}
