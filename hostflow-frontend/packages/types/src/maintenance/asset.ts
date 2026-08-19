// Mirrors module-maintenance's AssetResponse / CreateAssetRequest /
// CreateMaintenanceScheduleRequest.

import type { MaintenanceCategory } from "./work-order";

export interface AssetResponse {
  id: string;
  name: string;
  category: string | null;
  serialNumber: string | null;
  warrantyExpiryDate: string | null;
  underWarranty: boolean;
}

export interface CreateAssetRequest {
  propertyId: string;
  name: string;
  category?: string;
  serialNumber?: string;
  purchaseDate?: string;
  warrantyExpiryDate?: string;
}

export interface CreateMaintenanceScheduleRequest {
  propertyId: string;
  assetId?: string;
  category: MaintenanceCategory;
  title: string;
  intervalDays: number;
  firstDueDate: string;
}
