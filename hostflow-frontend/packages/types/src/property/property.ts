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

// Set explicitly by the owner at creation, never derived purely from
// PropertyType -- OFFICE/RETAIL_MALL listings are genuinely ambiguous (a hall
// might be a day-rate conference venue or a monthly-rent workspace lease).
// NIGHTLY keeps the existing check-in/check-out Booking flow; MONTHLY drops
// it in favor of module-rental's Lease-based system (see RentalInquiry*).
export type RentalModel = "NIGHTLY" | "MONTHLY";

export interface PropertyResponse {
  id: string;
  name: string;
  description: string | null;
  propertyType: PropertyType;
  rentalModel: RentalModel;
  status: PropertyStatus;
  addressLine: string;
  city: string;
  country: string;
  latitude: number | null;
  longitude: number | null;
  basePrice: string | null;
  /** Owner/manager-set "in use" override, independent of Booking/Lease data. */
  manualOccupiedUntil: string | null;
  /** Owner-entered unit inventory (e.g. 10 units, 6 occupied) -- not derived
   *  from Booking/Lease data. Defaults to 1 total / 0 occupied. */
  totalUnits: number;
  occupiedUnits: number;
  occupancyPercent: number;
}

export interface CreatePropertyRequest {
  name: string;
  propertyType: PropertyType;
  rentalModel: RentalModel;
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
  totalUnits?: number;
  occupiedUnits?: number;
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
  rentalModel: RentalModel;
  addressLine: string;
  city: string;
  country: string;
  latitude: number | null;
  longitude: number | null;
  basePrice: string | null;
  manualOccupiedUntil: string | null;
}
