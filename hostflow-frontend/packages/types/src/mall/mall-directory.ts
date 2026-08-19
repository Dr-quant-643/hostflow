// Mirrors PublicVenueDirectoryController.StoreDirectoryEntry — a minimal
// public-readable projection distinct from module-mall's staff-facing
// retail tenant DTOs.

export interface StoreDirectoryEntry {
  unitNumber: string;
  businessName: string;
}
