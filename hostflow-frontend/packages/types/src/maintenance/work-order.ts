// Mirrors module-maintenance's WorkOrderResponse / CreateWorkOrderRequest.

export type MaintenanceCategory =
  | "PLUMBING"
  | "ELECTRICAL"
  | "HVAC"
  | "APPLIANCE"
  | "STRUCTURAL"
  | "PEST_CONTROL"
  | "CLEANING"
  | "OTHER";

export type WorkOrderPriority = "LOW" | "MEDIUM" | "HIGH" | "EMERGENCY";

export type WorkOrderStatus = "OPEN" | "ASSIGNED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";

export interface WorkOrderResponse {
  id: string;
  propertyId: string;
  category: MaintenanceCategory;
  title: string;
  description: string | null;
  priority: WorkOrderPriority;
  status: WorkOrderStatus;
  assignedTechnicianUserId: string | null;
  resolutionNotes: string | null;
}

export interface CreateWorkOrderRequest {
  propertyId: string;
  category: MaintenanceCategory;
  title: string;
  description?: string;
  priority: WorkOrderPriority;
}
