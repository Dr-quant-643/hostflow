// Mirrors module-identity's RegisterGuestRequest / GuestProfileResponse.

export interface RegisterGuestRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
}

export interface GuestProfileResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}
