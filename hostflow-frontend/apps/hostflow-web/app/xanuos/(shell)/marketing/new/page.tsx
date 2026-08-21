import { PageHeader } from "@hostflow/ui";
import { CampaignForm } from "@/components/xanuos/marketing/campaign-form";

export default function NewCampaignPage() {
  return (
    <div>
      <PageHeader title="New Campaign" />
      <CampaignForm />
    </div>
  );
}
