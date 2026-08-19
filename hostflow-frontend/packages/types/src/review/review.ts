// Mirrors module-review's ReviewResponse / OwnerResponseRequest.

export interface ReviewResponse {
  id: string;
  propertyId: string;
  bookingId: string;
  rating: number;
  comment: string | null;
  ownerResponse: string | null;
}

export interface OwnerResponseRequest {
  response: string;
}

export interface CreateReviewRequest {
  bookingId: string;
  rating: number;
  comment?: string;
}
