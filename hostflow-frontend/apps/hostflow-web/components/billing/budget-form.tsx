"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { budgetFormSchema, type BudgetFormValues } from "@hostflow/validation";
import { Button, Input, Select, Stack } from "@hostflow/ui";
import { useSetBudget } from "@hostflow/api-client/src/hooks/use-billing";
import type { PropertyResponse } from "@hostflow/types";

const CATEGORY_OPTIONS = [
  { value: "MAINTENANCE", label: "Maintenance" },
  { value: "UTILITIES", label: "Utilities" },
  { value: "STAFF", label: "Staff" },
  { value: "MARKETING", label: "Marketing" },
  { value: "INSURANCE", label: "Insurance" },
  { value: "TAXES", label: "Taxes" },
  { value: "SUPPLIES", label: "Supplies" },
  { value: "OTHER", label: "Other" },
];

function firstOfMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-01`;
}

export function BudgetForm({ properties }: { properties: PropertyResponse[] }) {
  const setBudget = useSetBudget();

  const form = useForm<BudgetFormValues>({
    resolver: zodResolver(budgetFormSchema),
    defaultValues: {
      propertyId: properties[0]?.id,
      category: "OTHER",
      budgetMonth: firstOfMonth(),
      allocatedAmount: "0.00",
    },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await setBudget.mutateAsync(values);
      toast.success("Budget set");
    } catch {
      toast.error("Failed to set budget");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Select
          label="Property"
          {...form.register("propertyId")}
          options={properties.map((p) => ({ value: p.id, label: p.name }))}
        />
        <Select
          label="Category"
          {...form.register("category")}
          options={CATEGORY_OPTIONS}
        />
        <Input label="Month" type="date" {...form.register("budgetMonth")} />
        <Input
          label="Allocated Amount"
          {...form.register("allocatedAmount")}
          error={form.formState.errors.allocatedAmount?.message}
        />
        <Button type="submit" loading={setBudget.isPending}>
          Set Budget
        </Button>
      </Stack>
    </form>
  );
}
