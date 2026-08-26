"use client";

import { Card, Stack, Badge, Button, Skeleton, EmptyState, toast } from "@hostflow/ui";
import { usePricingSuggestions } from "@hostflow/api-client/src/hooks/use-analytics";
import { useUpdatePropertyDetails } from "@hostflow/api-client/src/hooks/use-properties";
import type { PricingSuggestionRow } from "@hostflow/types";
import { formatKES } from "@/lib/currency";

function ApplyButton({ suggestion }: { suggestion: PricingSuggestionRow }) {
  const update = useUpdatePropertyDetails(suggestion.propertyId);
  if (suggestion.changePercent === 0) {
    return <span className="text-xs text-muted-foreground">No change needed</span>;
  }
  return (
    <Button
      size="sm"
      loading={update.isPending}
      onClick={async () => {
        try {
          await update.mutateAsync({ basePrice: suggestion.suggestedPrice });
          toast.success(`Price updated to ${formatKES(suggestion.suggestedPrice)}`);
        } catch {
          toast.error("Couldn't update the price");
        }
      }}
    >
      Apply {formatKES(suggestion.suggestedPrice)}
    </Button>
  );
}

function SuggestionCard({ suggestion }: { suggestion: PricingSuggestionRow }) {
  const tone = suggestion.changePercent > 0 ? "success" : suggestion.changePercent < 0 ? "warning" : "outline";
  return (
    <Card className="p-5">
      <Stack gap="sm">
        <Stack direction="row" gap="sm" align="center" className="justify-between">
          <p className="font-medium">{suggestion.propertyName}</p>
          <Badge variant={tone}>
            {suggestion.changePercent > 0 ? "+" : ""}
            {suggestion.changePercent}%
          </Badge>
        </Stack>
        <Stack direction="row" gap="sm" align="center">
          <span className="text-sm text-muted-foreground line-through">{formatKES(suggestion.currentPrice)}</span>
          {suggestion.changePercent !== 0 && (
            <span className="text-lg font-semibold">{formatKES(suggestion.suggestedPrice)}</span>
          )}
        </Stack>
        <p className="text-sm text-muted-foreground">{suggestion.reason}</p>
        <ApplyButton suggestion={suggestion} />
      </Stack>
    </Card>
  );
}

export function PricingSuggestions() {
  const { data, isLoading, isError } = usePricingSuggestions();

  if (isLoading) return <Skeleton className="h-64 w-full" />;
  if (isError) return <EmptyState title="Couldn't load pricing suggestions" description="Try refreshing." />;
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No pricing suggestions yet"
        description="Suggestions appear for active, priced, nightly-rate properties."
      />
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {data.map((s) => (
        <SuggestionCard key={s.propertyId} suggestion={s} />
      ))}
    </div>
  );
}
