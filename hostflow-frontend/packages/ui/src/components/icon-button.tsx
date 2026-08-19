import * as React from "react";
import { Button, type ButtonProps } from "./button";
import { cn } from "../lib/cn";

export interface IconButtonProps extends Omit<ButtonProps, "size"> {
  "aria-label": string;
}

export const IconButton = React.forwardRef<HTMLButtonElement, IconButtonProps>(
  ({ className, ...props }, ref) => {
    return (
      <Button
        ref={ref}
        size="icon"
        className={cn("shrink-0", className)}
        {...props}
      />
    );
  },
);
IconButton.displayName = "IconButton";
