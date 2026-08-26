"use client";

import { Badge } from "@hostflow/ui";
import { useMyLoyaltyStatus } from "@hostflow/api-client/src/hooks/use-analytics";

// Shows the guest their own standing with THIS property's owner --
// deliberately quiet for NEW/AT_RISK (no badge at all; not every guest
// needs a status shown to them) and only surfaces something for VIP/REPEAT,
// where it reads as recognition rather than a sales pitch.
export function LoyaltyBadge({ propertyId }: { propertyId: string }) {
  const { data } = useMyLoyaltyStatus(propertyId);
  if (!data) return null;

  if (data.segment === "VIP") {
    return (
      <Badge variant="default" className="w-fit">
        🌟 VIP guest with this host
      </Badge>
    );
  }
  if (data.segment === "REPEAT") {
    return (
      <Badge variant="secondary" className="w-fit">
        👋 Welcome back — stay #{data.totalStays + 1} with this host
      </Badge>
    );
  }
  return null;
}
