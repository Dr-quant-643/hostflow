export interface Coordinates {
  latitude: number;
  longitude: number;
}

// Haversine great-circle distance in kilometers.
export function distanceKm(a: Coordinates, b: Coordinates): number {
  const R = 6371;
  const dLat = toRad(b.latitude - a.latitude);
  const dLon = toRad(b.longitude - a.longitude);
  const lat1 = toRad(a.latitude);
  const lat2 = toRad(b.latitude);

  const h =
    Math.sin(dLat / 2) ** 2 +
    Math.sin(dLon / 2) ** 2 * Math.cos(lat1) * Math.cos(lat2);
  return R * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
}

function toRad(deg: number): number {
  return (deg * Math.PI) / 180;
}

export function formatDistance(km: number): string {
  if (km < 1) return "< 1 km away";
  if (km < 10) return `${km.toFixed(1)} km away`;
  return `${Math.round(km)} km away`;
}

// Opens the user's own maps app (Google Maps on the web, which iOS/Android
// hand off to Apple Maps/Google Maps natively) for real turn-by-turn
// navigation — works with zero API key, unlike an embedded route preview.
export function externalDirectionsUrl(origin: Coordinates, destination: Coordinates): string {
  const params = new URLSearchParams({
    api: "1",
    origin: `${origin.latitude},${origin.longitude}`,
    destination: `${destination.latitude},${destination.longitude}`,
    travelmode: "driving",
  });
  return `https://www.google.com/maps/dir/?${params.toString()}`;
}

// Same as externalDirectionsUrl but with no known origin — Google Maps
// falls back to the device's current location as the starting point when
// opened with permission granted, so this still works without us having
// called the geolocation API ourselves (e.g. from a trip card, where we
// haven't asked for location access).
export function externalDirectionsUrlToDestination(destination: Coordinates): string {
  const params = new URLSearchParams({
    api: "1",
    destination: `${destination.latitude},${destination.longitude}`,
    travelmode: "driving",
  });
  return `https://www.google.com/maps/dir/?${params.toString()}`;
}

// Rough drive-time estimate (straight-line distance / average urban+highway
// speed) shown before a real routed ETA is available (i.e. no Mapbox token
// yet, or before the Directions API call resolves).
export function estimateDriveMinutes(km: number): number {
  const avgKmh = km < 15 ? 35 : 65;
  return Math.max(1, Math.round((km / avgKmh) * 60));
}

export function formatDriveTime(minutes: number): string {
  if (minutes < 60) return `~${minutes} min drive`;
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `~${h}h${m > 0 ? ` ${m}m` : ""} drive`;
}
