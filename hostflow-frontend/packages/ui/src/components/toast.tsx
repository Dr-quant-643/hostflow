"use client";

import * as React from "react";
// Type-only import: erased at compile time, so it does NOT create a runtime
// dependency on sonner (the whole point of the Function("return require")()
// trick below is to keep sonner a soft/optional dependency downstream). This
// just gives the exported `toast` its real shape (toast.success/.error/etc.)
// instead of the loose `(...args: any[]) => any` that was hiding those
// methods from every call site typed against this export.
import type { toast as SonnerToastFn } from "sonner";

type SonnerModule = {
  toast: (...args: any[]) => any;
  Toaster: (props?: any) => React.ReactElement | null;
};

const sonner = (() => {
  try {
    return Function("return require")()("sonner") as SonnerModule;
  } catch {
    return {
      toast: (...args: any[]) => {
        if (typeof console !== "undefined") {
          console.warn(
            "sonner is not installed; toast calls are ignored.",
            args,
          );
        }
      },
      Toaster: () => null,
    } satisfies SonnerModule;
  }
})();

const { toast: rawToast, Toaster: SonnerToaster } = sonner;

// Re-export a thin wrapper so app code imports from @hostflow/ui, not sonner
// directly — keeps the toast implementation swappable later.
export const toast = rawToast as typeof SonnerToastFn;

export function Toaster() {
  return (
    <SonnerToaster
      position="bottom-right"
      toastOptions={{
        classNames: {
          toast: "!bg-card !text-card-foreground !border-border",
          title: "!text-sm !font-medium",
          description: "!text-sm !text-muted-foreground",
        },
      }}
    />
  );
}
