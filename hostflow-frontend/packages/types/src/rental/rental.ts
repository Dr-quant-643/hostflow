// Mirrors module-rental's RentalTenantResponse / LeaseResponse / RentPaymentResponse.

export type LeaseStatus = "DRAFT" | "ACTIVE" | "EXPIRED" | "TERMINATED" | "DECLINED";
export type RentPaymentStatus = "DUE" | "PAID" | "LATE" | "WAIVED";

export interface RentalTenantResponse {
  id: string;
  fullName: string;
  email: string | null;
  phone: string | null;
}

export interface CreateRentalTenantRequest {
  fullName: string;
  email?: string;
  phone?: string;
}

export interface LeaseResponse {
  id: string;
  propertyId: string;
  tenantIdRef: string;
  startDate: string;
  endDate: string;
  monthlyRent: string;
  securityDeposit: string | null;
  status: LeaseStatus;
  /** Set only when status is DECLINED -- the owner's reason for not going ahead. */
  declineReason: string | null;
}

export interface CreateLeaseRequest {
  propertyId: string;
  tenantIdRef: string;
  startDate: string;
  endDate: string;
  monthlyRent: string;
  securityDeposit?: string;
}

export interface RentPaymentResponse {
  id: string;
  leaseId: string;
  dueDate: string;
  amount: string;
  status: RentPaymentStatus;
  paidDate: string | null;
}

export type RentalInquiryStatus = "OPEN" | "REPLIED";

// Owner-facing shape (RentalInquiryResponse.java) -- scoped to a single
// property via ?propertyId=, so no propertyName field.
export interface RentalInquiryResponse {
  id: string;
  propertyId: string;
  guestUserId: string;
  message: string;
  status: RentalInquiryStatus;
  replyMessage: string | null;
  repliedAt: string | null;
}

// Guest-facing shape (RentalInquiryOrchestrator.MyRentalInquiryRow) -- spans
// multiple properties, so includes propertyName. Also reused for the owner's
// global FIFO queue (/mine-as-owner), which returns the identical row shape.
export interface MyRentalInquiry {
  id: string;
  propertyId: string;
  propertyName: string;
  message: string;
  status: RentalInquiryStatus;
  replyMessage: string | null;
}

// Guest's own leases (RentalPortalQueries.MyLeaseRow), resolved via
// RentalTenant.linked_user_id -- GET /rental/portal/my-leases.
export interface MyLeaseRow {
  id: string;
  propertyId: string;
  startDate: string;
  endDate: string;
  monthlyRent: string;
  status: LeaseStatus;
  declineReason: string | null;
}
