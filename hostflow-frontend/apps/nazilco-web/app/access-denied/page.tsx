import { Stack, PageHeader, Button } from "@hostflow/ui";
import Link from "next/link";

export default function AccessDeniedPage() {
  return (
    <Stack gap="lg" className="p-6 text-center">
      <PageHeader
        title="Access denied"
        description="Your account doesn't have permission to use this page."
      />
      <Button asChild>
        <Link href="/">Back to Home</Link>
      </Button>
    </Stack>
  );
}
