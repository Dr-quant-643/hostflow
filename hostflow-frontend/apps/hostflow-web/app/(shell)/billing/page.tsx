import Link from "next/link";
import { PageHeader, Button } from "@hostflow/ui";
import { InvoiceList } from "@/components/billing/invoice-list";

export default function BillingPage() {
  return (
    <div>
      <PageHeader
        title="Billing"
        description="Invoices and payments"
        actions={
          <Button asChild variant="outline">
            <Link href="/billing/batch-import">Batch Import</Link>
          </Button>
        }
      />
      <InvoiceList />
    </div>
  );
}
