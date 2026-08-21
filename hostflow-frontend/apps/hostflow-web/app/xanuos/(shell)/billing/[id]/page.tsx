"use client";

import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Button, toast } from "@hostflow/ui";
import {
  useInvoice,
  useIssueInvoice,
  useMarkInvoicePaid,
} from "@hostflow/api-client/src/hooks/use-billing";
import { PaymentSection } from "@/components/xanuos/billing/payment-section";

export default function InvoiceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: invoice, isLoading, isError } = useInvoice(id);
  const issue = useIssueInvoice(id);
  const markPaid = useMarkInvoicePaid(id);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !invoice) return <EmptyState title="Invoice not found" />;

  return (
    <Stack gap="md">
      <PageHeader
        title={`Invoice ${invoice.id.slice(0, 8)}`}
        description={`Due ${invoice.dueDate}`}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge>{invoice.status}</Badge>
            {invoice.status === "DRAFT" && (
              <Button
                loading={issue.isPending}
                onClick={async () => {
                  try {
                    await issue.mutateAsync();
                    toast.success("Invoice issued");
                  } catch {
                    toast.error("Failed to issue invoice");
                  }
                }}
              >
                Issue
              </Button>
            )}
            {invoice.status === "ISSUED" && (
              <Button
                loading={markPaid.isPending}
                onClick={async () => {
                  try {
                    await markPaid.mutateAsync();
                    toast.success("Invoice marked as paid");
                  } catch {
                    toast.error("Failed to mark as paid");
                  }
                }}
              >
                Mark Paid
              </Button>
            )}
          </Stack>
        }
      />
      <p className="text-lg font-medium">${invoice.amount}</p>
      <PaymentSection invoiceId={invoice.id} />
    </Stack>
  );
}
