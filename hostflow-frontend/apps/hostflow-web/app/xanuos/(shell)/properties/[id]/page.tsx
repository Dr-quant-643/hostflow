"use client";

import { useParams } from "next/navigation";
import { PageHeader, Skeleton, EmptyState, Badge, Stack, Card, Button, toast } from "@hostflow/ui";
import {
  useProperty,
  usePublishProperty,
  useArchiveProperty,
  useUnarchiveProperty,
} from "@hostflow/api-client/src/hooks/use-properties";
import { ApiError } from "@hostflow/api-client/src/errors";
import { DocumentUploadForm } from "@/components/xanuos/properties/document-upload-form";
import { DocumentList } from "@/components/xanuos/properties/document-list";
import { ReviewList } from "@/components/xanuos/properties/review-list";
import { PropertyDetailsForm } from "@/components/xanuos/properties/property-details-form";
import { OccupancyControl } from "@/components/xanuos/properties/occupancy-control";
import { InquiryList } from "@/components/xanuos/properties/inquiry-list";
import { formatKES } from "@/lib/currency";

export default function PropertyDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: property, isLoading, isError } = useProperty(id);
  const publish = usePublishProperty(id);
  const archive = useArchiveProperty(id);
  const unarchive = useUnarchiveProperty(id);

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
                  } catch (err) {
                    toast.error(err instanceof ApiError ? err.message : "Failed to publish");
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
            {property.status === "ARCHIVED" && (
              <Button
                variant="outline"
                loading={unarchive.isPending}
                onClick={async () => {
                  try {
                    await unarchive.mutateAsync();
                    toast.success("Property restored to draft — publish it again when ready");
                  } catch {
                    toast.error("Failed to unarchive");
                  }
                }}
              >
                Unarchive
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
            <span className="font-medium text-warning">Not ready to publish yet</span> — add a{" "}
            {property.rentalModel === "MONTHLY" ? "monthly rent" : "nightly rate"} and location below
            (a price is required before you can publish).
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

      {property.rentalModel === "MONTHLY" && (
        <Card>
          <Stack gap="md">
            <h3 className="font-medium">Rental Inquiries</h3>
            <p className="text-sm text-muted-foreground">
              Prospective tenants who&apos;ve expressed interest in renting this property.
            </p>
            <InquiryList propertyId={id} />
          </Stack>
        </Card>
      )}

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
