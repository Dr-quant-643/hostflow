import * as React from "react";
import { Sparkles } from "lucide-react";
import { Card, CardContent } from "./card";
import { Badge } from "./badge";
import { cn } from "../lib/cn";

export interface CampaignCardProps extends React.HTMLAttributes<HTMLDivElement> {
  name: string;
  platform: string;
  status: "DRAFT" | "GENERATING" | "READY" | "PUBLISHED" | "FAILED";
}

const statusVariant = {
  DRAFT: "secondary",
  GENERATING: "default",
  READY: "success",
  PUBLISHED: "success",
  FAILED: "destructive",
} as const;

export const CampaignCard = React.forwardRef<HTMLDivElement, CampaignCardProps>(
  ({ className, name, platform, status, ...props }, ref) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="flex items-center justify-between p-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <Sparkles className="h-4 w-4 text-accent" />
            <p className="font-medium">{name}</p>
          </div>
          <p className="text-sm text-muted-foreground">{platform}</p>
        </div>
        <Badge variant={statusVariant[status]}>{status}</Badge>
      </CardContent>
    </Card>
  ),
);
CampaignCard.displayName = "CampaignCard";
