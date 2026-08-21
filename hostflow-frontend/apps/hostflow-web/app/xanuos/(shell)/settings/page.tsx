"use client";

import { PageHeader, Card, Stack, Badge } from "@hostflow/ui";
import { useSession } from "@hostflow/auth/src/use-session";

export default function SettingsPage() {
  const { user } = useSession();

  return (
    <Stack gap="lg">
      <PageHeader
        title="Settings"
        description="Organization and account details"
      />
      <Card>
        <Stack gap="sm">
          <h3 className="font-medium">Account</h3>
          <p className="text-sm text-muted-foreground">{user?.name}</p>
          <p className="text-sm text-muted-foreground">{user?.email}</p>
        </Stack>
      </Card>
      <Card>
        <Stack gap="sm">
          <h3 className="font-medium">Product Access</h3>
          <Stack direction="row" gap="sm">
            {user?.productScope?.map((scope) => (
              <Badge key={scope}>{scope}</Badge>
            ))}
          </Stack>
        </Stack>
      </Card>
    </Stack>
  );
}
