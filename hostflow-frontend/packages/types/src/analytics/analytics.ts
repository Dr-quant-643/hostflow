// Mirrors module-analytics's PropertyOccupancyResponse / MonthlyRevenueResponse.

export interface PropertyOccupancyResponse {
  propertyId: string;
  propertyName: string;
  totalBookings: number;
  totalNightsBooked: number;
  totalRevenue: string; // BigDecimal as string
}

export interface MonthlyRevenueResponse {
  month: string; // ISO "YYYY-MM"
  invoicedTotal: string;
  paidTotal: string;
  invoiceCount: number;
}

// Mirrors GuestSegmentQueries.GuestSegmentRow. The RFM-lite guest/tenant
// segmentation engine -- live-computed over this owner's own bookings +
// leases (see the backend class's own doc comment for why: not a materialized
// view, not a cross-tenant guest profile, not a page-view event pipeline).
export type GuestSegment = "ACTIVE_TENANT" | "AT_RISK" | "VIP" | "REPEAT" | "NEW";

export interface GuestSegmentRow {
  guestUserId: string;
  name: string | null;
  email: string | null;
  totalBookings: number;
  totalReservations: number;
  totalSpend: string;
  firstActivityDate: string | null;
  lastActivityDate: string | null;
  recencyDays: number | null;
  hasActiveLease: boolean;
  segment: GuestSegment;
}

// Mirrors SegmentCampaign -- a message an owner sends to every guest
// currently in a given segment (or "ALL"), closing the loop on segmentation
// being actionable instead of read-only.
export type SegmentCampaignStatus = "DRAFT" | "SENT";
export type SegmentCampaignTarget = GuestSegment | "ALL";

export interface SegmentCampaignResponse {
  id: string;
  targetSegment: SegmentCampaignTarget;
  subject: string;
  body: string;
  status: SegmentCampaignStatus;
  recipientCount: number | null;
  sentAt: string | null;
}

export interface CreateSegmentCampaignRequest {
  targetSegment: SegmentCampaignTarget;
  subject: string;
  body: string;
}
