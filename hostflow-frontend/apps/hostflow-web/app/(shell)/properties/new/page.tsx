import { PageHeader } from "@hostflow/ui";
import { PropertyForm } from "@/components/properties/property-form";

export default function NewPropertyPage() {
  return (
    <div>
      <PageHeader title="Add Property" />
      <PropertyForm />
    </div>
  );
}
