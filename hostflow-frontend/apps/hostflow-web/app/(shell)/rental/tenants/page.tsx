import { PageHeader, Stack } from "@hostflow/ui";
import { TenantForm } from "@/components/rental/tenant-form";
import { TenantList } from "@/components/rental/tenant-list";

export default function RentalTenantsPage() {
  return (
    <Stack gap="lg">
      <PageHeader title="Rental Tenants" description="People renting your properties" />
      <TenantForm />
      <TenantList />
    </Stack>
  );
}
