import { PageHeader } from "@hostflow/ui";
import { SupportList } from "@/components/nazilco-admin/support/support-list";

export default function SupportPage() {
  return (
    <div>
      <PageHeader title="Support" description="Guest support requests" />
      <SupportList />
    </div>
  );
}
