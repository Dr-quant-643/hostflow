"use client";

import { Skeleton, EmptyState, Badge, Button, Stack, Card, toast } from "@hostflow/ui";
import {
  usePropertyDocuments,
  useDeletePropertyDocument,
} from "@hostflow/api-client/src/hooks/use-property-documents";

export function DocumentList({ propertyId }: { propertyId: string }) {
  const { data, isLoading, isError } = usePropertyDocuments(propertyId);
  const deleteDocument = useDeletePropertyDocument(propertyId);

  if (isLoading) return <Skeleton className="h-32 w-full" />;
  if (isError) {
    return (
      <EmptyState title="Couldn't load documents" description="Try refreshing." />
    );
  }
  if (!data || data.length === 0) {
    return (
      <EmptyState
        title="No documents yet"
        description="Upload photos, floor plans, or contracts using the form above."
      />
    );
  }

  return (
    <Stack gap="sm">
      {data.map((doc) => (
        <Card key={doc.id}>
          <Stack direction="row" justify="between" align="center">
            <Stack direction="row" gap="sm" align="center">
              <Badge>{doc.documentType}</Badge>
              <a
                href={doc.url}
                target="_blank"
                rel="noreferrer"
                className="text-sm font-medium text-primary hover:underline"
              >
                {doc.fileName}
              </a>
            </Stack>
            <Button
              variant="ghost"
              size="sm"
              onClick={async () => {
                try {
                  await deleteDocument.mutateAsync(doc.id);
                  toast.success("Document deleted");
                } catch {
                  toast.error("Failed to delete document");
                }
              }}
            >
              Delete
            </Button>
          </Stack>
        </Card>
      ))}
    </Stack>
  );
}
