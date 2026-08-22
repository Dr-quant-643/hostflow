"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import {
  campaignFormSchema,
  type CampaignFormValues,
} from "@hostflow/validation";
import { Button, Input, Select, Textarea, Stack, toast } from "@hostflow/ui";
import { useCreateCampaign } from "@hostflow/api-client/src/hooks/use-marketing";

export function CampaignForm() {
  const router = useRouter();
  const createCampaign = useCreateCampaign();

  const form = useForm<CampaignFormValues>({
    resolver: zodResolver(campaignFormSchema),
    defaultValues: { name: "", platform: "EMAIL", content: "" },
  });

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      const created = await createCampaign.mutateAsync(values);
      toast.success("Campaign created");
      router.push(`/xanuos/marketing/${created.id}`);
    } catch {
      toast.error("Failed to create campaign");
    }
  });

  return (
    <form onSubmit={onSubmit}>
      <Stack gap="md">
        <Input
          label="Campaign Name"
          {...form.register("name")}
          error={form.formState.errors.name?.message}
        />
        <Select
          label="Platform"
          {...form.register("platform")}
          options={[
            { value: "EMAIL", label: "Email" },
            { value: "WHATSAPP", label: "WhatsApp" },
            { value: "FACEBOOK", label: "Facebook" },
            { value: "INSTAGRAM", label: "Instagram" },
            { value: "TIKTOK", label: "TikTok" },
            { value: "GOOGLE_ADS", label: "Google Ads" },
            { value: "BLOG", label: "Blog" },
          ]}
        />
        <Textarea
          label="Content"
          {...form.register("content")}
          error={form.formState.errors.content?.message}
          placeholder="Write the campaign copy — this is published as-is, no AI generation."
        />
        <Button type="submit" loading={createCampaign.isPending}>
          Create Campaign
        </Button>
      </Stack>
    </form>
  );
}
