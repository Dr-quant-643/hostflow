"use client";

import { useState } from "react";
import { Button, Select, Input, Stack, toast } from "@hostflow/ui";
import { useUploadPropertyDocument } from "@hostflow/api-client/src/hooks/use-property-documents";
import type { PropertyDocumentType } from "@hostflow/types";

const DOCUMENT_TYPE_OPTIONS: { value: PropertyDocumentType; label: string }[] = [
  { value: "PHOTO", label: "Photo" },
  { value: "FLOOR_PLAN", label: "Floor Plan" },
  { value: "CONTRACT", label: "Contract" },
  { value: "INSURANCE", label: "Insurance" },
  { value: "OTHER", label: "Other" },
];

export function DocumentUploadForm({ propertyId }: { propertyId: string }) {
  const [file, setFile] = useState<File | null>(null);
  const [documentType, setDocumentType] = useState<PropertyDocumentType>("PHOTO");
  const upload = useUploadPropertyDocument(propertyId);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    try {
      await upload.mutateAsync({ file, documentType });
      toast.success("Document uploaded");
      setFile(null);
    } catch {
      toast.error(
        documentType === "PHOTO" || documentType === "FLOOR_PLAN"
          ? "Upload failed — photos and floor plans must be JPEG/PNG/WebP under 10MB"
          : "Upload failed — documents must be an image or PDF under 25MB",
      );
    }
  };

  return (
    <form onSubmit={onSubmit}>
      <Stack direction="row" gap="sm" align="end">
        <Select
          label="Document Type"
          value={documentType}
          onChange={(e) => setDocumentType(e.target.value as PropertyDocumentType)}
          options={DOCUMENT_TYPE_OPTIONS}
        />
        <Input
          label="File"
          type="file"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
        />
        <Button type="submit" disabled={!file} loading={upload.isPending}>
          Upload
        </Button>
      </Stack>
    </form>
  );
}
