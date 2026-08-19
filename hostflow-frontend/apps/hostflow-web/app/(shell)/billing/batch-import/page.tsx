import { PageHeader } from "@hostflow/ui";
import { BatchInvoiceImport } from "@/components/billing/batch-invoice-import";

export default function BatchImportPage() {
  return (
    <div>
      <PageHeader
        title="Batch Import Invoices"
        description="Upload a CSV of up to 100 invoices"
      />
      <BatchInvoiceImport />
    </div>
  );
}
