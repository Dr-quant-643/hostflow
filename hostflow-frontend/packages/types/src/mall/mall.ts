// Mirrors module-mall's RetailUnitResponse / RetailTenantResponse / MallEventResponse / ParkingSessionResponse.

export type RetailUnitStatus = "VACANT" | "OCCUPIED" | "UNDER_RENOVATION";

export interface RetailUnitResponse {
  id: string;
  unitNumber: string;
  sizeSqm: string | null;
  status: RetailUnitStatus;
}

export interface CreateRetailUnitRequest {
  propertyId: string;
  unitNumber: string;
  sizeSqm?: string;
}

export interface RetailTenantResponse {
  id: string;
  retailUnitId: string;
  businessName: string;
  monthlyRent: string;
}

export interface AssignRetailTenantRequest {
  retailUnitId: string;
  businessName: string;
  contactEmail?: string;
  contactPhone?: string;
  monthlyRent: string;
  revenueSharePercent?: string;
}

export interface MallEventResponse {
  id: string;
  title: string;
  description: string | null;
  startsAt: string;
  endsAt: string;
}

export interface CreateMallEventRequest {
  propertyId: string;
  title: string;
  description?: string;
  startsAt: string;
  endsAt: string;
}

export type ParkingSessionStatus = "ACTIVE" | "COMPLETED";

export interface ParkingSessionResponse {
  id: string;
  vehiclePlate: string;
  enteredAt: string;
  exitedAt: string | null;
  feeCharged: string | null;
  status: ParkingSessionStatus;
}

export interface ParkingEntryRequest {
  propertyId: string;
  vehiclePlate: string;
}
