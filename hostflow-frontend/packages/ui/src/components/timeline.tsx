import * as React from "react";
import { cn } from "../lib/cn";

export interface TimelineEvent {
  id: string;
  title: string;
  description?: string;
  timestamp: string;
  icon?: React.ReactNode;
}

export interface TimelineProps extends React.HTMLAttributes<HTMLDivElement> {
  events: TimelineEvent[];
}

export const Timeline = React.forwardRef<HTMLDivElement, TimelineProps>(
  ({ className, events, ...props }, ref) => (
    <div ref={ref} className={cn("space-y-6", className)} {...props}>
      {events.map((event, i) => (
        <div key={event.id} className="relative flex gap-4">
          <div className="flex flex-col items-center">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-muted text-muted-foreground">
              {event.icon}
            </div>
            {i < events.length - 1 && <div className="w-px flex-1 bg-border" />}
          </div>
          <div className="pb-6">
            <p className="text-sm font-medium">{event.title}</p>
            {event.description && (
              <p className="text-sm text-muted-foreground">
                {event.description}
              </p>
            )}
            <p className="mt-1 text-xs text-muted-foreground">
              {event.timestamp}
            </p>
          </div>
        </div>
      ))}
    </div>
  ),
);
Timeline.displayName = "Timeline";
