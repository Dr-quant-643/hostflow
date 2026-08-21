"use client";

import { useState } from "react";
import { PageHeader, Input, Stack, Skeleton, EmptyState } from "@hostflow/ui";
import { useMyOrgUsers, useSearchMyOrgUsers } from "@hostflow/api-client/src/hooks/use-team";
import { TeamMemberCard } from "@/components/xanuos/team/team-member-card";

export default function TeamPage() {
  const [query, setQuery] = useState("");
  const list = useMyOrgUsers();
  const search = useSearchMyOrgUsers(query);

  const { data, isLoading, isError } = query ? search : list;

  return (
    <Stack gap="lg">
      <PageHeader title="My Team" description="Manage your organization's staff" />
      <Input
        placeholder="Search by name or email..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />

      {isLoading && <Skeleton className="h-64 w-full" />}
      {!isLoading && isError && (
        <EmptyState title="Couldn't load team members" description="Try refreshing." />
      )}
      {!isLoading && data && data.content.length === 0 && (
        <EmptyState title="No team members found" />
      )}
      {!isLoading &&
        data &&
        data.content.length > 0 && (
          <Stack gap="sm">
            {data.content.map((user) => (
              <TeamMemberCard key={user.id} user={user} />
            ))}
          </Stack>
        )}
    </Stack>
  );
}
