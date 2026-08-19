// Mirrors module-crm's SupportTicketResponse / CreateSupportTicketRequest /
// TicketPriority / TicketStatus / TicketProductScope.

export type TicketPriority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";
export type TicketStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";
export type TicketProductScope = "XANUOS" | "NAZILCO";

export interface SupportTicketResponse {
  id: string;
  contactId: string | null;
  subject: string;
  description: string | null;
  priority: TicketPriority;
  status: TicketStatus;
  productScope: TicketProductScope;
  assignedToUserId: string | null;
}

export interface CreateSupportTicketRequest {
  contactId?: string;
  subject: string;
  description?: string;
  priority: TicketPriority;
  productScope: TicketProductScope;
}
