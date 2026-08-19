// Mirrors module-crm's ContactResponse / CreateContactRequest / InteractionResponse.

export type ContactStatus = "LEAD" | "QUALIFIED" | "CUSTOMER" | "LOST";

export type InteractionType =
  | "CALL"
  | "EMAIL"
  | "MEETING"
  | "NOTE"
  | "WHATSAPP_MESSAGE"
  | "SYSTEM_EVENT"
  | "SUPPORT_REQUEST";

export interface ContactResponse {
  id: string;
  fullName: string;
  email?: string;
  phone?: string;
  source?: string;
  status: ContactStatus;
}

export interface CreateContactRequest {
  fullName: string;
  email?: string;
  phone?: string;
  source?: string;
}

export interface InteractionResponse {
  id: string;
  contactId: string;
  loggedByUserId: string;
  type: InteractionType;
  notes?: string;
  occurredAt: string;
}

export interface LogInteractionRequest {
  type: InteractionType;
  notes?: string;
}
