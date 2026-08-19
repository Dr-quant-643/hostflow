import { PageHeader } from "@hostflow/ui";
import { ContactForm } from "@/components/crm/contact-form";

export default function NewContactPage() {
  return (
    <div>
      <PageHeader title="Add Contact" />
      <ContactForm />
    </div>
  );
}
