import * as React from "react";
import { ArrowUp, ArrowDown } from "lucide-react";
import { Card, CardContent } from "./card";
import { cn } from "../lib/cn";

// The number carries color (the "which way is this going" signal); the label
// stays in muted text always -- same split as a stock ticker: bold colored
// figures, gray words. "info" is for a neutral-but-notable count (nothing
// wrong, just worth noticing), distinct from "default" (plain figure).
export type StatCardTone = "default" | "info" | "success" | "warning" | "destructive";

const TONE_CLASS: Record<StatCardTone, string> = {
  default: "text-foreground",
  info: "text-sapphire-600 dark:text-sapphire-400",
  success: "text-success",
  warning: "text-warning",
  destructive: "text-destructive",
};

export interface StatCardProps extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  value: string | number;
  change?: number;
  icon?: React.ReactNode;
  tone?: StatCardTone;
}

export const StatCard = React.forwardRef<HTMLDivElement, StatCardProps>(
  ({ className, label, value, change, icon, tone = "default", ...props }, ref) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="p-6">
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">{label}</p>
            <p className={cn("text-2xl font-semibold tracking-tight", TONE_CLASS[tone])}>{value}</p>
            {change !== undefined && (
              <div
                className={cn(
                  "flex items-center gap-1 text-xs font-medium",
                  change >= 0 ? "text-success" : "text-destructive",
                )}
              >
                {change >= 0 ? (
                  <ArrowUp className="h-3 w-3" />
                ) : (
                  <ArrowDown className="h-3 w-3" />
                )}
                {Math.abs(change)}%
              </div>
            )}
          </div>
          {icon && <div className="text-muted-foreground">{icon}</div>}
        </div>
      </CardContent>
    </Card>
  ),
);
StatCard.displayName = "StatCard";
