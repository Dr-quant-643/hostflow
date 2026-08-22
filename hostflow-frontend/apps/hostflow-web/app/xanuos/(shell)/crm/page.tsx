import Link from "next/link";
import { PageHeader, Button } from "@hostflow/ui";
import { ContactList } from "@/components/xanuos/crm/contact-list";

export default function CrmPage() {
  return (
    <div>
      <PageHeader
        title="CRM"
        description="Contacts and lead pipeline"
        actions={
          <Button asChild>
            <Link href="/xanuos/crm/new">Add Contact</Link>
          </Button>
        }
      />
      <ContactList />
    </div>
  );
}
