import * as React from "react";
import { Heart, Star, MapPin } from "lucide-react";
import { Card } from "./card";
import { Badge } from "./badge";
import { cn } from "../lib/cn";

export interface PropertyCardProps extends React.HTMLAttributes<HTMLDivElement> {
  imageUrl: string;
  name: string;
  location: string;
  pricePerNight: string;
  rating?: number;
  status?: "PUBLISHED" | "DRAFT" | "ARCHIVED";
  favorited?: boolean;
  onToggleFavorite?: () => void;
}

const statusVariant = {
  PUBLISHED: "success",
  DRAFT: "secondary",
  ARCHIVED: "outline",
} as const;

export const PropertyCard = React.forwardRef<HTMLDivElement, PropertyCardProps>(
  (
    {
      className,
      imageUrl,
      name,
      location,
      pricePerNight,
      rating,
      status,
      favorited,
      onToggleFavorite,
      ...props
    },
    ref,
  ) => (
    <Card ref={ref} className={cn("overflow-hidden", className)} {...props}>
      <div className="relative aspect-[4/3] w-full">
        <img src={imageUrl} alt={name} className="h-full w-full object-cover" />
        {onToggleFavorite && (
          <button
            onClick={onToggleFavorite}
            aria-label={
              favorited ? "Remove from favorites" : "Add to favorites"
            }
            className="absolute right-3 top-3 rounded-full bg-background/80 p-1.5 backdrop-blur-sm"
          >
            <Heart
              className={cn(
                "h-4 w-4",
                favorited && "fill-destructive text-destructive",
              )}
            />
          </button>
        )}
        {status && (
          <Badge
            variant={statusVariant[status]}
            className="absolute left-3 top-3"
          >
            {status}
          </Badge>
        )}
      </div>
      <div className="space-y-1 p-4">
        <div className="flex items-start justify-between gap-2">
          <p className="font-medium">{name}</p>
          {rating !== undefined && (
            <div className="flex shrink-0 items-center gap-1 text-sm">
              <Star className="h-3.5 w-3.5 fill-current" />
              {rating.toFixed(1)}
            </div>
          )}
        </div>
        <div className="flex items-center gap-1 text-sm text-muted-foreground">
          <MapPin className="h-3.5 w-3.5" />
          {location}
        </div>
        <p className="pt-1 text-sm font-medium">
          {pricePerNight}{" "}
          <span className="font-normal text-muted-foreground">/ night</span>
        </p>
      </div>
    </Card>
  ),
);
PropertyCard.displayName = "PropertyCard";
