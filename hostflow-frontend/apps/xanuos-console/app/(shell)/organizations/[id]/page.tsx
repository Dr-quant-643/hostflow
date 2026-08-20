"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { PageHeader,
  Skeleton,
  EmptyState,
  Badge,
  Stack,
  Card,
  Input,
  Button,, toast } from "@hostflow/ui";
import {
  useOrganization,
  useRenameOrganization,
} from "@hostflow/api-client/src/hooks/use-console-organizations";

// Rename is the ONLY mutation OrganizationController exposes for an
// existing org — there is no suspend/deactivate/reactivate endpoint
// anywhere in the backend, so this page doesn't offer one.
export default function OrganizationDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: org, isLoading, isError } = useOrganization(id);
  const rename = useRenameOrganization();
  const [name, setName] = useState("");

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !org) return <EmptyState title="Organization not found" />;

  return (
    <Stack gap="lg">
      <PageHeader
        title={org.name}
        description={org.slug}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge variant="outline">{org.primaryProduct}</Badge>
            <Badge variant={org.active ? "success" : "outline"}>
              {org.active ? "Active" : "Inactive"}
            </Badge>
          </Stack>
        }
      />
      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Rename Organization</h3>
          <Input
            placeholder={org.name}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <Button
            disabled={!name.trim() || name.trim() === org.name}
            loading={rename.isPending}
            onClick={async () => {
              try {
                await rename.mutateAsync({ orgId: org.id, name: name.trim() });
                toast.success("Organization renamed");
                setName("");
              } catch {
                toast.error("Failed to rename organization");
              }
            }}
          >
            Save Name
          </Button>
        </Stack>
      </Card>
    </Stack>
  );
}
