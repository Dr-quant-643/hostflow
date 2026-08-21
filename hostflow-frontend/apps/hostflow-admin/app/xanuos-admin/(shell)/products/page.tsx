import { PageHeader, EmptyState } from "@hostflow/ui";

// Deliberately NOT wired to useProductPlans() with a real list rendering —
// building a full CRUD UI against a guessed data shape would create more
// false confidence than value. This page states the gap plainly instead.
export default function ProductsPage() {
  return (
    <div>
      <PageHeader title="Products" description="Plan and tier management" />
      <EmptyState
        title="Not yet defined"
        description="No product/plan/subscription entity exists in the backend yet. This section needs a real data model before UI work continues here."
      />
    </div>
  );
}
