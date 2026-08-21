"use client";

import * as React from "react";
import { toast as rawToast, Toaster as SonnerToaster } from "sonner";

// Re-export a thin wrapper so app code imports from @hostflow/ui, not sonner
// directly — keeps the toast implementation swappable later. sonner is a
// real, hard dependency of this package (see package.json), so a plain
// static import is all this ever needed — a prior version of this file
// tried to load it via `Function("return require")()` to keep it a "soft"
// optional dependency, but `require` doesn't exist at runtime in a browser
// bundle at all, so that always threw and silently fell back to a no-op
// stub with no .error/.success methods, breaking every toast call in every
// app that used them.
export const toast = rawToast;

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
