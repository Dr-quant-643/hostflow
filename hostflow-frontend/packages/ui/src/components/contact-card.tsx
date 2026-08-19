import * as React from "react";
import { Mail, Phone } from "lucide-react";
import { Card, CardContent } from "./card";
import { Avatar, AvatarFallback } from "./avatar";
import { Badge } from "./badge";
import { cn } from "../lib/cn";

export interface ContactCardProps extends React.HTMLAttributes<HTMLDivElement> {
  fullName: string;
  email?: string;
  phone?: string;
  stage: "NEW" | "QUALIFIED" | "CUSTOMER" | "LOST";
}

const stageVariant = {
  NEW: "secondary",
  QUALIFIED: "default",
  CUSTOMER: "success",
  LOST: "destructive",
} as const;

function initials(name: string) {
  return name
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

export const ContactCard = React.forwardRef<HTMLDivElement, ContactCardProps>(
  ({ className, fullName, email, phone, stage, ...props }, ref) => (
    <Card ref={ref} className={cn(className)} {...props}>
      <CardContent className="flex items-center gap-3 p-4">
        <Avatar>
          <AvatarFallback>{initials(fullName)}</AvatarFallback>
        </Avatar>
        <div className="flex-1 space-y-0.5">
          <div className="flex items-center gap-2">
            <p className="font-medium">{fullName}</p>
            <Badge variant={stageVariant[stage]}>{stage}</Badge>
          </div>
          <div className="flex items-center gap-3 text-xs text-muted-foreground">
            {email && (
              <span className="flex items-center gap-1">
                <Mail className="h-3 w-3" />
                {email}
              </span>
            )}
            {phone && (
              <span className="flex items-center gap-1">
                <Phone className="h-3 w-3" />
                {phone}
              </span>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  ),
);
ContactCard.displayName = "ContactCard";
