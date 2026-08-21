import Link from "next/link";
import { PageHeader, Button } from "@hostflow/ui";
import { CampaignList } from "@/components/xanuos/marketing/campaign-list";

export default function MarketingPage() {
  return (
    <div>
      <PageHeader
        title="Marketing"
        description="Campaigns and content"
        actions={
          <Button asChild>
            <Link href="/marketing/new">New Campaign</Link>
          </Button>
        }
      />
      <CampaignList />
    </div>
  );
}
