// Mirrors PublicVenueDirectoryController.MeetingRoomEntry — public-readable,
// distinct from module-office's staff-facing meeting room DTOs.

export interface PublicMeetingRoom {
  id: string;
  name: string;
  capacity: number;
}
