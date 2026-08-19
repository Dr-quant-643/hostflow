// Mirrors module-property's PropertyResponse / CreatePropertyRequest.

// publish() actually sets ACTIVE, not PUBLISHED — PUBLISHED/INACTIVE exist in
// the enum but are never set by any reachable service method.
export type PropertyStatus = "DRAFT" | "ACTIVE" | "PUBLISHED" | "INACTIVE" | "ARCHIVED";

export type PropertyType =
  | "RESIDENTIAL"
  | "HOTEL"
  | "VACATION_RENTAL"
  | "OFFICE"
  | "RETAIL_MALL"
  | "MIXED_USE";

export interface PropertyResponse {
  id: string;
  name: string;
  description: string | null;
  propertyType: PropertyType;
  status: PropertyStatus;
  addressLine: string;
  city: string;
  country: string;
  latitude: number | null;
  longitude: number | null;
  basePrice: string | null;
}

export interface CreatePropertyRequest {
  name: string;
  propertyType: PropertyType;
  addressLine: string;
  city: string;
  country: string;
}

// PATCH /api/v1/properties/{id} — partial update, only provided fields change.
export interface UpdatePropertyDetailsRequest {
  description?: string;
  basePrice?: string;
  latitude?: number;
  longitude?: number;
}

// PropertyDocumentType/PropertyDocumentResponse live in ./property-document.ts.

// Mirrors PublicPropertyQueries.PublicPropertyRow exactly — the guest-facing
// public property endpoints are plain cross-tenant JDBC projections, not the
// full PropertyResponse shape. No heroImageUrl/nightlyRate/maxGuests/amenities
// fields exist here: photos are a separate endpoint (GET .../photos), pricing
// is basePrice (not a computed nightly rate), and there is no bedroom/bathroom/
// guest-capacity/amenity data anywhere in the property schema.
export interface PublicPropertySummary {
  id: string;
  name: string;
  description: string | null;
  propertyType: PropertyType;
  addressLine: string;
  city: string;
  country: string;
  latitude: number | null;
  longitude: number | null;
  basePrice: string | null;
}
