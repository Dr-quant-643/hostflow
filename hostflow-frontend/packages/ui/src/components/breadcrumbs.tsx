import * as React from "react";
import { ChevronRight } from "lucide-react";
import { cn } from "../lib/cn";

export interface BreadcrumbItem {
  label: string;
  href?: string;
}

export interface BreadcrumbsProps extends React.HTMLAttributes<HTMLElement> {
  items: BreadcrumbItem[];
  // ComponentType<any> rather than a `{href: string; ...}`-shaped generic:
  // Next.js's Link types `href` as `Url` (string | UrlObject), which makes
  // its `propTypes` field structurally incompatible with any component type
  // whose props narrow `href` to `string`, even though passing Link here is
  // always valid at runtime. `any` sidesteps that structural comparison.
  LinkComponent?: React.ComponentType<any>;
}

export const Breadcrumbs = React.forwardRef<HTMLElement, BreadcrumbsProps>(
  ({ className, items, LinkComponent, ...props }, ref) => {
    const Anchor =
      LinkComponent ??
      ("a" as unknown as NonNullable<BreadcrumbsProps["LinkComponent"]>);
    return (
      <nav
        ref={ref}
        aria-label="Breadcrumb"
        className={cn("flex", className)}
        {...props}
      >
        <ol className="flex items-center gap-1.5 text-sm text-muted-foreground">
          {items.map((item, i) => {
            const isLast = i === items.length - 1;
            return (
              <li key={item.label} className="flex items-center gap-1.5">
                {item.href && !isLast ? (
                  <Anchor href={item.href} className="hover:text-foreground">
                    {item.label}
                  </Anchor>
                ) : (
                  <span className={cn(isLast && "font-medium text-foreground")}>
                    {item.label}
                  </span>
                )}
                {!isLast && <ChevronRight className="h-3.5 w-3.5" />}
              </li>
            );
          })}
        </ol>
      </nav>
    );
  },
);
Breadcrumbs.displayName = "Breadcrumbs";
