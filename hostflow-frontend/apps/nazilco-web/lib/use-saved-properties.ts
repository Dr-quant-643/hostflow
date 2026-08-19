"use client";

import { useCallback, useEffect, useState } from "react";

const STORAGE_KEY = "nazilco:saved-properties";
const CHANGE_EVENT = "nazilco:saved-properties-changed";

function readIds(): string[] {
  if (typeof window === "undefined") return [];
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as string[]) : [];
  } catch {
    return [];
  }
}

function writeIds(ids: string[]) {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));
  window.dispatchEvent(new Event(CHANGE_EVENT));
}

// No guest-favorites endpoint exists on the backend yet, so this is a
// localStorage-backed "saved properties" list — same UX from the guest's
// point of view (persists across visits on this device), swappable for a
// real per-account API later without touching call sites.
export function useSavedProperties() {
  const [ids, setIds] = useState<string[]>([]);

  useEffect(() => {
    setIds(readIds());
    const onChange = () => setIds(readIds());
    window.addEventListener(CHANGE_EVENT, onChange);
    window.addEventListener("storage", onChange);
    return () => {
      window.removeEventListener(CHANGE_EVENT, onChange);
      window.removeEventListener("storage", onChange);
    };
  }, []);

  const toggle = useCallback((id: string) => {
    const current = readIds();
    const next = current.includes(id) ? current.filter((x) => x !== id) : [...current, id];
    writeIds(next);
    setIds(next);
  }, []);

  const isSaved = useCallback((id: string) => ids.includes(id), [ids]);

  return { savedIds: ids, isSaved, toggle };
}
