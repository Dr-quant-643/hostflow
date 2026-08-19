import { Stack, PageHeader, Button } from "@hostflow/ui";
import Link from "next/link";

export default function AccessDeniedPage() {
  return (
    <Stack gap="lg" className="p-6 text-center">
      <PageHeader
        title="Access denied"
        description="Your account doesn't have permission to use this app."
      />
      <Button asChild>
        <Link href="/api/auth/logout">Log out</Link>
      </Button>
    </Stack>
  );
}
