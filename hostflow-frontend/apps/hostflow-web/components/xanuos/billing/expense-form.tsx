"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { expenseFormSchema, type ExpenseFormValues } from "@hostflow/validation";
import { Button, Input, Select, Stack, toast } from "@hostflow/ui";
import { useCreateExpense } from "@hostflow/api-client/src/hooks/use-billing";

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

export function ExpenseForm({ propertyId }: { propertyId: string }) {
  const createExpense = useCreateExpense();

  const form = useForm<ExpenseFormValues>({
    resolver: zodResolver(expenseFormSchema),
    defaultValues: {
      propertyId,
      category: "OTHER",
      description: "",
      amount: "0.00",
      expenseDate: new Date().toISOString().slice(0, 10),
    },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await createExpense.mutateAsync({ ...values, propertyId });
      toast.success("Expense recorded");
      form.reset({
        propertyId,
        category: "OTHER",
        description: "",
        amount: "0.00",
        expenseDate: new Date().toISOString().slice(0, 10),
      });
    } catch {
      toast.error("Failed to record expense");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Select
          label="Category"
          {...form.register("category")}
          options={CATEGORY_OPTIONS}
        />
        <Input
          label="Description"
          {...form.register("description")}
          error={form.formState.errors.description?.message}
        />
        <Input
          label="Amount"
          {...form.register("amount")}
          error={form.formState.errors.amount?.message}
        />
        <Input label="Date" type="date" {...form.register("expenseDate")} />
        <Button type="submit" loading={createExpense.isPending}>
          Record
        </Button>
      </Stack>
    </form>
  );
}
