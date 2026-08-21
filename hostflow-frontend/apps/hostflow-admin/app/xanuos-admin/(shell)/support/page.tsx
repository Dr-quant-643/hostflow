import { PageHeader } from "@hostflow/ui";
import { SupportList } from "@/components/xanuos-admin/support/support-list";

export default function SupportPage() {
  return (
    <div>
      <PageHeader
        title="Support"
        description="XanuOS support tickets"
      />
      <SupportList />
    </div>
  );
}
