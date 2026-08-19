import { PageHeader } from "@hostflow/ui";
import { OrganizationList } from "@/components/organizations/organization-list";

export default function OrganizationsPage() {
  return (
    <div>
      <PageHeader
        title="Organizations"
        description="All tenant organizations across HostFlow"
      />
      <OrganizationList />
    </div>
  );
}
