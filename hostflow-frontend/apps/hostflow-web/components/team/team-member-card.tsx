"use client";

import { useState } from "react";
import { Card, Stack, Badge, Button, Checkbox, toast } from "@hostflow/ui";
import { useUpdateUserRoles, useDeactivateUser } from "@hostflow/api-client/src/hooks/use-team";
import type { OrgUserSummaryResponse, UserRole } from "@hostflow/types";

const ASSIGNABLE_ROLES: UserRole[] = ["XANUOS_OWNER", "XANUOS_MANAGER", "XANUOS_STAFF"];

export function TeamMemberCard({ user }: { user: OrgUserSummaryResponse }) {
  const [roles, setRoles] = useState<UserRole[]>(user.roles);
  const updateRoles = useUpdateUserRoles();
  const deactivate = useDeactivateUser();

  const dirty = JSON.stringify([...roles].sort()) !== JSON.stringify([...user.roles].sort());

  return (
    <Card>
      <Stack gap="sm">
        <Stack direction="row" justify="between" align="center">
          <Stack direction="row" gap="sm" align="center">
            <span className="font-medium">
              {user.firstName} {user.lastName}
            </span>
            <span className="text-sm text-muted-foreground">{user.email}</span>
            <Badge variant={user.active ? "default" : "outline"}>
              {user.active ? "Active" : "Deactivated"}
            </Badge>
          </Stack>
          {user.active && (
            <Button
              size="sm"
              variant="destructive"
              loading={deactivate.isPending}
              onClick={async () => {
                try {
                  await deactivate.mutateAsync(user.id);
                  toast.success("Staff member deactivated");
                } catch {
                  toast.error("Failed to deactivate");
                }
              }}
            >
              Deactivate
            </Button>
          )}
        </Stack>

        <Stack direction="row" gap="sm" align="center">
          {ASSIGNABLE_ROLES.map((role) => (
            <label key={role} className="flex items-center gap-1.5 text-sm">
              <Checkbox
                checked={roles.includes(role)}
                onCheckedChange={(checked) =>
                  setRoles((prev) =>
                    checked ? [...prev, role] : prev.filter((r) => r !== role),
                  )
                }
              />
              {role.replace("XANUOS_", "")}
            </label>
          ))}
          {dirty && (
            <Button
              size="sm"
              loading={updateRoles.isPending}
              onClick={async () => {
                try {
                  await updateRoles.mutateAsync({ userId: user.id, roles });
                  toast.success("Roles updated");
                } catch {
                  toast.error("Failed to update roles");
                }
              }}
            >
              Save Roles
            </Button>
          )}
        </Stack>
      </Stack>
    </Card>
  );
}
