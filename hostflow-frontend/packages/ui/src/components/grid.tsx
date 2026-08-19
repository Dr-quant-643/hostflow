import * as React from "react";
import { cn } from "../lib/cn";

export type GridGap = 2 | 4 | 6 | 8 | "xs" | "sm" | "md" | "lg" | "xl";

export interface GridProps extends React.HTMLAttributes<HTMLDivElement> {
  cols?: 1 | 2 | 3 | 4 | 6 | 12;
  gap?: GridGap;
}

const colsMap = {
  1: "grid-cols-1",
  2: "grid-cols-2",
  3: "grid-cols-3",
  4: "grid-cols-4",
  6: "grid-cols-6",
  12: "grid-cols-12",
};
const gapMap: Record<GridGap, string> = {
  2: "gap-2",
  4: "gap-4",
  6: "gap-6",
  8: "gap-8",
  xs: "gap-1",
  sm: "gap-2",
  md: "gap-4",
  lg: "gap-6",
  xl: "gap-8",
};

export const Grid = React.forwardRef<HTMLDivElement, GridProps>(
  ({ className, cols = 1, gap = 4, ...props }, ref) => (
    <div
      ref={ref}
      className={cn("grid", colsMap[cols], gapMap[gap], className)}
      {...props}
    />
  ),
);
Grid.displayName = "Grid";
