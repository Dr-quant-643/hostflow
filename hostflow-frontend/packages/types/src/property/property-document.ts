// Mirrors module-property's PropertyDocumentResponse.

export type PropertyDocumentType =
  | "PHOTO"
  | "VIDEO"
  | "FLOOR_PLAN"
  | "CONTRACT"
  | "INSURANCE"
  | "OTHER";

export interface PropertyDocument {
  id: string;
  fileName: string;
  contentType: string;
  documentType: PropertyDocumentType;
  url: string;
}

export type PropertyDocumentResponse = PropertyDocument;
