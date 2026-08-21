import { PageHeader } from "@hostflow/ui";
import { AdminInvoiceList } from "@/components/xanuos-admin/billing/admin-invoice-list";

export default function AdminBillingPage() {
  return (
    <div>
      <PageHeader
        title="Billing"
        description="Cross-organization invoice oversight"
      />
      <AdminInvoiceList />
    </div>
  );
}
