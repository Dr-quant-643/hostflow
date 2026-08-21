import { PageHeader, Stack } from "@hostflow/ui";
import { TenantForm } from "@/components/xanuos/rental/tenant-form";
import { TenantList } from "@/components/xanuos/rental/tenant-list";

export default function RentalTenantsPage() {
  return (
    <Stack gap="lg">
      <PageHeader title="Rental Tenants" description="People renting your properties" />
      <TenantForm />
      <TenantList />
    </Stack>
  );
}
