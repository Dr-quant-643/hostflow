import * as React from "react";
import { Calendar, Users } from "lucide-react";
import { Card, CardContent } from "./card";
import { Badge } from "./badge";
import { cn } from "../lib/cn";

export interface BookingCardProps extends React.HTMLAttributes<HTMLDivElement> {
  propertyName: string;
  checkIn: string;
  checkOut: string;
  guests: number;
  status: "PENDING" | "CONFIRMED" | "CHECKED_IN" | "CHECKED_OUT" | "CANCELLED";
  totalPrice: string;
}

const statusVariant = {
  PENDING: "secondary",
  CONFIRMED: "success",
  CHECKED_IN: "default",
  CHECKED_OUT: "outline",
  CANCELLED: "destructive",
} as const;

export const BookingCard = React.forwardRef<HTMLDivElement, BookingCardProps>(
  (
    {
      className,
      propertyName,
      checkIn,
      checkOut,
      guests,
      status,
      totalPrice,
      ...props
    },
    ref,
  ) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="flex items-center justify-between gap-4 p-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <p className="font-medium">{propertyName}</p>
            <Badge variant={statusVariant[status]}>{status}</Badge>
          </div>
          <div className="flex items-center gap-3 text-sm text-muted-foreground">
            <span className="flex items-center gap-1">
              <Calendar className="h-3.5 w-3.5" />
              {checkIn} → {checkOut}
            </span>
            <span className="flex items-center gap-1">
              <Users className="h-3.5 w-3.5" />
              {guests}
            </span>
          </div>
        </div>
        <p className="font-medium">{totalPrice}</p>
      </CardContent>
    </Card>
  ),
);
BookingCard.displayName = "BookingCard";
