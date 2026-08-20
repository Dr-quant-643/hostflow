"use client";

import { useState } from "react";
import { PageHeader,
  Stack,
  Card,
  Input,
  Button,
  Badge,
  Skeleton,
  EmptyState,, toast } from "@hostflow/ui";
import {
  useFeatureFlags,
  useSetGlobalFlag,
} from "@hostflow/api-client/src/hooks/use-console-feature-flags";

// Replaces the old Global Config stub — there was never a global-config
// entity in the backend, but a real FeatureFlagController does exist
// (module-platform-admin), so this is what "platform-wide settings" turned
// out to actually mean.
export default function FeatureFlagsPage() {
  const { data, isLoading, isError } = useFeatureFlags();
  const setFlag = useSetGlobalFlag();
  const [newKey, setNewKey] = useState("");

  const globalFlags = data?.filter((f) => f.scopeOrgId === null) ?? [];

  return (
    <Stack gap="lg">
      <PageHeader title="Feature Flags" description="Platform-wide feature toggles" />

      <Card>
        <Stack gap="sm" direction="row" align="end">
          <Input
            label="New flag key"
            value={newKey}
            onChange={(e) => setNewKey(e.target.value)}
            placeholder="e.g. new-marketing-module"
          />
          <Button
            disabled={!newKey.trim()}
            loading={setFlag.isPending}
            onClick={async () => {
              try {
                await setFlag.mutateAsync({ key: newKey.trim(), enabled: true });
                toast.success("Flag created");
                setNewKey("");
              } catch {
                toast.error("Failed to create flag");
              }
            }}
          >
            Add Flag (enabled)
          </Button>
        </Stack>
      </Card>

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load feature flags" description="Try refreshing." />
      )}
      {!isLoading && globalFlags.length === 0 && !isError && (
        <EmptyState title="No global flags defined yet" />
      )}
      {!isLoading && globalFlags.length > 0 && (
        <Stack gap="sm">
          {globalFlags.map((flag) => (
            <Card key={flag.id}>
              <Stack direction="row" justify="between" align="center">
                <Stack gap="sm">
                  <span className="font-medium">{flag.key}</span>
                  {flag.description && (
                    <span className="text-sm text-muted-foreground">{flag.description}</span>
                  )}
                </Stack>
                <Stack direction="row" gap="sm" align="center">
                  <Badge variant={flag.enabled ? "success" : "outline"}>
                    {flag.enabled ? "Enabled" : "Disabled"}
                  </Badge>
                  <Button
                    size="sm"
                    variant="outline"
                    loading={setFlag.isPending}
                    onClick={async () => {
                      try {
                        await setFlag.mutateAsync({
                          key: flag.key,
                          enabled: !flag.enabled,
                          description: flag.description ?? undefined,
                        });
                      } catch {
                        toast.error("Failed to update flag");
                      }
                    }}
                  >
                    {flag.enabled ? "Disable" : "Enable"}
                  </Button>
                </Stack>
              </Stack>
            </Card>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
