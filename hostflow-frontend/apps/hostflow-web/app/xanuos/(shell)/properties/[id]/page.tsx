"use client";

import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Card, Button, toast } from "@hostflow/ui";
import {
  useProperty,
  usePublishProperty,
  useArchiveProperty,
} from "@hostflow/api-client/src/hooks/use-properties";
import { DocumentUploadForm } from "@/components/xanuos/properties/document-upload-form";
import { DocumentList } from "@/components/xanuos/properties/document-list";
import { ReviewList } from "@/components/xanuos/properties/review-list";
import { PropertyDetailsForm } from "@/components/xanuos/properties/property-details-form";
import { OccupancyControl } from "@/components/xanuos/properties/occupancy-control";
import { formatKES } from "@/lib/currency";

export default function PropertyDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: property, isLoading, isError } = useProperty(id);
  const publish = usePublishProperty(id);
  const archive = useArchiveProperty(id);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !property) {
    return <EmptyState title="Property not found" />;
  }

  return (
    <Stack gap="lg">
      <PageHeader
        title={property.name}
        description={`${property.city}, ${property.country}`}
        actions={
          <Stack direction="row" gap="sm" align="center">
            <Badge>{property.status}</Badge>
            {property.status === "DRAFT" && (
              <Button
                loading={publish.isPending}
                onClick={async () => {
                  try {
                    await publish.mutateAsync();
                    toast.success("Property published");
                  } catch {
                    toast.error("Failed to publish");
                  }
                }}
              >
                Publish
              </Button>
            )}
            {property.status !== "ARCHIVED" && (
              <Button
                variant="outline"
                loading={archive.isPending}
                onClick={async () => {
                  try {
                    await archive.mutateAsync();
                    toast.success("Property archived");
                  } catch {
                    toast.error("Failed to archive");
                  }
                }}
              >
                Archive
              </Button>
            )}
          </Stack>
        }
      />
      {property.status === "ACTIVE" && (
        <Card className="border-success/30 bg-success/5 p-4">
          <p className="text-sm">
            <span className="font-medium text-success">Live on NazilCo</span>
            <span className="text-muted-foreground"> — guests can find and book this stay now.</span>
          </p>
        </Card>
      )}
      {property.status === "DRAFT" && (!property.basePrice || property.latitude == null) && (
        <Card className="border-warning/30 bg-warning/5 p-4">
          <p className="text-sm text-muted-foreground">
            <span className="font-medium text-warning">Not ready to publish yet</span> — add a
            nightly rate and location below so guests can find and book it once published.
          </p>
        </Card>
      )}

      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Listing details</h3>
          {property.basePrice && (
            <p className="text-sm text-muted-foreground">
              Current rate: <span className="font-medium text-foreground">{formatKES(property.basePrice)}</span>{" "}
              / {property.rentalModel === "MONTHLY" ? "month" : "night"}
            </p>
          )}
          <PropertyDetailsForm property={property} />
        </Stack>
      </Card>

      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Occupancy</h3>
          <OccupancyControl property={property} />
        </Stack>
      </Card>

      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Photos</h3>
          <p className="text-sm text-muted-foreground">
            The first photo becomes the cover image guests see on NazilCo.
          </p>
          <DocumentUploadForm propertyId={id} />
          <DocumentList propertyId={id} />
        </Stack>
      </Card>

      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Reviews</h3>
          <ReviewList propertyId={id} />
        </Stack>
      </Card>
    </Stack>
  );
}
