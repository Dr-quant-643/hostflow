import Link from "next/link";
import { PageHeader, Button } from "@hostflow/ui";
import { PropertyList } from "@/components/xanuos/properties/property-list";

export default function PropertiesPage() {
  return (
    <div>
      <PageHeader
        title="Properties"
        description="Manage your property portfolio"
        actions={
          <Button asChild>
            <Link href="/properties/new">Add Property</Link>
          </Button>
        }
      />
      <PropertyList />
    </div>
  );
}
