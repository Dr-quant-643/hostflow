import { PageHeader } from "@hostflow/ui";
import { UserAuthorityList } from "@/components/access/user-authority-list";

export default function AccessControlPage() {
  return (
    <div>
      <PageHeader
        title="Access Control"
        description="User roles and product authorities"
      />
      <UserAuthorityList />
    </div>
  );
}
