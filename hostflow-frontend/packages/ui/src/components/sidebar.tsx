"use client";

import * as React from "react";
import { cn } from "../lib/cn";

export interface SidebarNavItem {
  label: string;
  href: string;
  icon?: React.ReactNode;
  active?: boolean;
}

export interface SidebarProps extends React.HTMLAttributes<HTMLDivElement> {
  items: SidebarNavItem[];
  logo?: React.ReactNode;
  LinkComponent?: React.ComponentType<{
    href: string;
    className?: string;
    children: React.ReactNode;
  }>;
  collapsed?: boolean;
}

export const Sidebar = React.forwardRef<HTMLDivElement, SidebarProps>(
  (
    { className, items, logo, LinkComponent, collapsed = false, ...props },
    ref,
  ) => {
    const Anchor =
      LinkComponent ??
      ("a" as unknown as NonNullable<SidebarProps["LinkComponent"]>);
    return (
      <div
        ref={ref}
        className={cn(
          "flex h-full flex-col border-r border-border bg-card transition-all",
          collapsed ? "w-16" : "w-64",
          className,
        )}
        {...props}
      >
        {logo && (
          <div className="flex h-16 items-center border-b border-border px-4">
            {logo}
          </div>
        )}
        <nav className="flex-1 space-y-1 p-2">
          {items.map((item) => (
            <Anchor
              key={item.href}
              href={item.href}
              className={cn(
                "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                item.active
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:bg-muted hover:text-foreground",
              )}
            >
              {item.icon}
              {!collapsed && <span>{item.label}</span>}
            </Anchor>
          ))}
        </nav>
      </div>
    );
  },
);
Sidebar.displayName = "Sidebar";
