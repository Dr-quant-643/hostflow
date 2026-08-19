import * as React from "react";
import { Card, CardContent } from "./card";
import { cn } from "../lib/cn";

// Distinct from StatCard: MetricCard is for analytics-report contexts
// (no trend arrow, just label + value + optional sub-label) — e.g.
// analytics summary tiles rather than dashboard KPI tiles.
export interface MetricCardProps extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  value: string | number;
  subLabel?: string;
}

export const MetricCard = React.forwardRef<HTMLDivElement, MetricCardProps>(
  ({ className, label, value, subLabel, ...props }, ref) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="space-y-1 p-6 text-center">
        <p className="text-sm text-muted-foreground">{label}</p>
        <p className="text-3xl font-semibold tracking-tight">{value}</p>
        {subLabel && (
          <p className="text-xs text-muted-foreground">{subLabel}</p>
        )}
      </CardContent>
    </Card>
  ),
);
MetricCard.displayName = "MetricCard";
