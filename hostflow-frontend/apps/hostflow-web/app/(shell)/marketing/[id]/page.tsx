"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { toast } from "sonner";
import {
  PageHeader,
  Skeleton,
  EmptyState,
  Badge,
  Stack,
  Button,
  Textarea,
  Card,
} from "@hostflow/ui";
import {
  useCampaign,
  usePublishCampaign,
  useArchiveCampaign,
  useUpdateCampaignContent,
} from "@hostflow/api-client/src/hooks/use-marketing";

export default function CampaignDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: campaign, isLoading, isError } = useCampaign(id);
  const publish = usePublishCampaign(id);
  const archive = useArchiveCampaign(id);
  const updateContent = useUpdateCampaignContent(id);

  const [content, setContent] = useState("");

  useEffect(() => {
    if (campaign) setContent(campaign.content);
  }, [campaign]);

  if (isLoading) return <Skeleton className="h-96 w-full" />;
  if (isError || !campaign) return <EmptyState title="Campaign not found" />;

  const isDraft = campaign.status === "DRAFT";
  const contentDirty = content !== campaign.content;

  return (
    <Stack gap="lg">
      <PageHeader
        title={campaign.name}
        description={campaign.platform}
        actions={
          <Stack direction="row" gap="sm">
            <Badge>{campaign.status}</Badge>
            {isDraft && (
              <Button
                loading={publish.isPending}
                onClick={async () => {
                  try {
                    await publish.mutateAsync();
                    toast.success("Campaign published");
                  } catch {
                    toast.error("Failed to publish");
                  }
                }}
              >
                Publish
              </Button>
            )}
            {campaign.status !== "ARCHIVED" && (
              <Button
                variant="outline"
                loading={archive.isPending}
                onClick={async () => {
                  try {
                    await archive.mutateAsync();
                    toast.success("Campaign archived");
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
      <Card>
        <Stack gap="md">
          <h3 className="font-medium">Content</h3>
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            disabled={!isDraft}
            rows={10}
          />
          {!isDraft && (
            <p className="text-sm text-muted-foreground">
              Content can only be edited while a campaign is in DRAFT.
            </p>
          )}
          {isDraft && (
            <Button
              disabled={!contentDirty}
              loading={updateContent.isPending}
              onClick={async () => {
                try {
                  await updateContent.mutateAsync(content);
                  toast.success("Content saved");
                } catch {
                  toast.error("Failed to save content");
                }
              }}
            >
              Save Content
            </Button>
          )}
        </Stack>
      </Card>
    </Stack>
  );
}
