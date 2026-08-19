import * as React from "react";
import { Card, CardContent } from "./card";
import { Badge } from "./badge";
import { cn } from "../lib/cn";

export interface InvoiceCardProps extends React.HTMLAttributes<HTMLDivElement> {
  invoiceNumber: string;
  amount: string;
  dueDate: string;
  status: "DRAFT" | "ISSUED" | "PAID" | "OVERDUE";
}

const statusVariant = {
  DRAFT: "secondary",
  ISSUED: "default",
  PAID: "success",
  OVERDUE: "destructive",
} as const;

export const InvoiceCard = React.forwardRef<HTMLDivElement, InvoiceCardProps>(
  ({ className, invoiceNumber, amount, dueDate, status, ...props }, ref) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="flex items-center justify-between p-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <p className="font-medium">{invoiceNumber}</p>
            <Badge variant={statusVariant[status]}>{status}</Badge>
          </div>
          <p className="text-sm text-muted-foreground">Due {dueDate}</p>
        </div>
        <p className="font-medium">{amount}</p>
      </CardContent>
    </Card>
  ),
);
InvoiceCard.displayName = "InvoiceCard";
