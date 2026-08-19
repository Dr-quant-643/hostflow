import * as React from "react";
import { LogOut } from "lucide-react";
import { cn } from "../lib/cn";

export interface TopbarProps extends React.HTMLAttributes<HTMLDivElement> {
  search?: React.ReactNode;
  actions?: React.ReactNode;
  /** Every AppTopbar across the 5 apps passes these — render the signed-in
   * user + logout directly rather than requiring every app to build its own
   * user-menu via `actions`. */
  userName?: string;
  userEmail?: string;
  onLogout?: () => void;
}

export const Topbar = React.forwardRef<HTMLDivElement, TopbarProps>(
  (
    { className, search, actions, userName, userEmail, onLogout, children, ...props },
    ref,
  ) => (
    <div
      ref={ref}
      className={cn(
        "flex h-16 items-center justify-between gap-4 border-b border-border bg-background px-4",
        className,
      )}
      {...props}
    >
      <div className="flex flex-1 items-center gap-4">
        {children}
        {search && <div className="max-w-md flex-1">{search}</div>}
      </div>
      <div className="flex items-center gap-3">
        {actions}
        {(userName || userEmail) && (
          <div className="flex items-center gap-3 border-l border-border pl-3">
            <div className="text-right leading-tight">
              {userName && <div className="text-sm font-medium">{userName}</div>}
              {userEmail && (
                <div className="text-xs text-muted-foreground">{userEmail}</div>
              )}
            </div>
            {onLogout && (
              <button
                type="button"
                onClick={onLogout}
                aria-label="Log out"
                className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground hover:bg-muted hover:text-foreground"
              >
                <LogOut className="h-4 w-4" />
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  ),
);
Topbar.displayName = "Topbar";
