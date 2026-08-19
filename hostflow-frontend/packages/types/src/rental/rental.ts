// Mirrors module-rental's RentalTenantResponse / LeaseResponse / RentPaymentResponse.

export type LeaseStatus = "DRAFT" | "ACTIVE" | "EXPIRED" | "TERMINATED";
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
