// Mirrors module-office's MeetingRoomResponse / RoomBookingResponse / VisitorResponse.

export interface MeetingRoomResponse {
  id: string;
  propertyId: string;
  name: string;
  capacity: number;
  active: boolean;
}

export interface CreateMeetingRoomRequest {
  propertyId: string;
  name: string;
  capacity: number;
}

export type RoomBookingStatus = "CONFIRMED" | "CANCELLED";

export interface RoomBookingResponse {
  id: string;
  roomId: string;
  startsAt: string;
  endsAt: string;
  purpose: string | null;
  status: RoomBookingStatus;
}

export interface CreateRoomBookingRequest {
  roomId: string;
  startsAt: string;
  endsAt: string;
  purpose?: string;
}

export type VisitorStatus = "EXPECTED" | "CHECKED_IN" | "CHECKED_OUT";

export interface VisitorResponse {
  id: string;
  fullName: string;
  company: string | null;
  expectedAt: string;
  checkedInAt: string | null;
  checkedOutAt: string | null;
  status: VisitorStatus;
}

export interface RegisterVisitorRequest {
  propertyId: string;
  fullName: string;
  company?: string;
  expectedAt: string;
}
