"use client";

import { useState } from "react";
import { Card, Stack, Input, Button, Badge, EmptyState, Skeleton, toast } from "@hostflow/ui";
import { useApiKeys, useCreateApiKey, useRevokeApiKey } from "@hostflow/api-client/src/hooks/use-developer";

function NewKeyReveal({ rawKey, onDismiss }: { rawKey: string; onDismiss: () => void }) {
  return (
    <Card className="border-warning/30 bg-warning/5 p-4">
      <Stack gap="sm">
        <p className="text-sm font-medium">Copy this key now -- you won&apos;t be able to see it again</p>
        <div className="flex items-center gap-2">
          <Input readOnly value={rawKey} className="font-mono text-xs" onFocus={(e) => e.target.select()} />
          <Button
            size="sm"
            onClick={() => {
              navigator.clipboard.writeText(rawKey);
              toast.success("Copied");
            }}
          >
            Copy
          </Button>
        </div>
        <Button size="sm" variant="outline" onClick={onDismiss} className="w-fit">
          Done
        </Button>
      </Stack>
    </Card>
  );
}

export function ApiKeyPanel() {
  const { data, isLoading } = useApiKeys();
  const create = useCreateApiKey();
  const revoke = useRevokeApiKey();
  const [name, setName] = useState("");
  const [revealedKey, setRevealedKey] = useState<string | null>(null);

  const onCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    try {
      const result = await create.mutateAsync({ name });
      setRevealedKey(result.rawKey);
      setName("");
    } catch {
      toast.error("Couldn't create the key");
    }
  };

  return (
    <Stack gap="md">
      {revealedKey && <NewKeyReveal rawKey={revealedKey} onDismiss={() => setRevealedKey(null)} />}

      <form onSubmit={onCreate}>
        <Stack direction="row" gap="sm">
          <Input placeholder="e.g. Analytics dashboard" value={name} onChange={(e) => setName(e.target.value)} />
          <Button type="submit" disabled={!name.trim()} loading={create.isPending}>
            Generate key
          </Button>
        </Stack>
      </form>

      {isLoading ? (
        <Skeleton className="h-24 w-full" />
      ) : !data || data.length === 0 ? (
        <EmptyState title="No API keys yet" description="Generate one above to read your data programmatically." />
      ) : (
        <Stack gap="sm">
          {data.map((key) => (
            <div key={key.id} className="flex items-center justify-between rounded-lg border border-border p-3">
              <Stack gap="xs">
                <p className="text-sm font-medium">{key.name}</p>
                <p className="font-mono text-xs text-muted-foreground">{key.keyPrefix}</p>
              </Stack>
              {key.revoked ? (
                <Badge variant="destructive">Revoked</Badge>
              ) : (
                <Button
                  size="sm"
                  variant="outline"
                  loading={revoke.isPending}
                  onClick={async () => {
                    try {
                      await revoke.mutateAsync(key.id);
                      toast.success("Key revoked");
                    } catch {
                      toast.error("Couldn't revoke");
                    }
                  }}
                >
                  Revoke
                </Button>
              )}
            </div>
          ))}
        </Stack>
      )}
    </Stack>
  );
}
