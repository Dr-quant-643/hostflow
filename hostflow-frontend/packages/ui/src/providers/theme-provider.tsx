"use client";

import * as React from "react";

type Mode = "light" | "dark" | "system";

interface ThemeContextValue {
  mode: Mode;
  setMode: (mode: Mode) => void;
}

const ThemeContext = React.createContext<ThemeContextValue | null>(null);

export function ThemeProvider({
  children,
  defaultMode = "system",
  storageKey = "hostflow-theme",
}: {
  children: React.ReactNode;
  defaultMode?: Mode;
  storageKey?: string;
}) {
  const [mode, setModeState] = React.useState<Mode>(defaultMode);

  React.useEffect(() => {
    const stored = window.localStorage.getItem(storageKey) as Mode | null;
    if (stored) setModeState(stored);
  }, [storageKey]);

  React.useEffect(() => {
    const root = window.document.documentElement;
    const resolved =
      mode === "system"
        ? window.matchMedia("(prefers-color-scheme: dark)").matches
          ? "dark"
          : "light"
        : mode;
    root.classList.toggle("dark", resolved === "dark");
  }, [mode]);

  const setMode = React.useCallback(
    (m: Mode) => {
      window.localStorage.setItem(storageKey, m);
      setModeState(m);
    },
    [storageKey],
  );

  return (
    <ThemeContext.Provider value={{ mode, setMode }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const ctx = React.useContext(ThemeContext);
  if (!ctx) throw new Error("useTheme must be used within ThemeProvider");
  return ctx;
}
