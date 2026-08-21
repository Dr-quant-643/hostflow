"use client";

import { useCallback, useState } from "react";
import type { Coordinates } from "./geo";

export type GeolocationStatus = "idle" | "requesting" | "granted" | "denied" | "unsupported";

export function useGeolocation() {
  const [status, setStatus] = useState<GeolocationStatus>("idle");
  const [coords, setCoords] = useState<Coordinates | null>(null);
  const [error, setError] = useState<string | null>(null);

  const request = useCallback(() => {
    if (typeof navigator === "undefined" || !navigator.geolocation) {
      setStatus("unsupported");
      return;
    }
    setStatus("requesting");
    setError(null);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoords({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        });
        setStatus("granted");
      },
      (err) => {
        setStatus("denied");
        setError(err.message || "Location access was denied");
      },
      { enableHighAccuracy: false, timeout: 10_000, maximumAge: 5 * 60_000 },
    );
  }, []);

  const clear = useCallback(() => {
    setCoords(null);
    setStatus("idle");
    setError(null);
  }, []);

  return { status, coords, error, request, clear };
}
