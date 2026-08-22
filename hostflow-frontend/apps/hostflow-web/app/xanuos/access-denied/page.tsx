"use client";

import { useState } from "react";
import { Stack, PageHeader, Button, Input, Card, toast } from "@hostflow/ui";
import { useClaimWorkspace } from "@hostflow/api-client/src/hooks/use-claim-workspace";
import { ApiError } from "@hostflow/api-client/src/errors";

export default function AccessDeniedPage() {
  const [organizationName, setOrganizationName] = useState("");
  const [finishing, setFinishing] = useState(false);
  const claim = useClaimWorkspace();

  const logOut = () => {
    // The route only accepts POST; a plain link would GET-navigate and 405.
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/xanuos/api/auth/logout";
    document.body.appendChild(form);
    form.submit();
  };

  const onCreateWorkspace = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!organizationName.trim()) return;
    try {
      await claim.mutateAsync({ organizationName: organizationName.trim() });
      // The session cookie still carries the pre-workspace claims -- refresh
      // it so PRODUCT_XANUOS actually takes effect before we navigate on.
      setFinishing(true);
      await fetch("/xanuos/api/auth/refresh", { method: "POST" });
      window.location.href = "/xanuos/dashboard";
    } catch (err) {
      const message =
        err instanceof ApiError ? err.message : "Couldn't create your workspace. Please try again.";
      toast.error(message);
    }
  };

  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6 text-center">
      <PageHeader
        title="No workspace linked to this account"
        description="You signed in successfully, but this account isn't attached to a XanuOS workspace yet. If you're a property owner or manager, create your own workspace below — or log out and sign in with the account your team invited you on."
      />
      <Card className="p-6 text-left">
        <form onSubmit={onCreateWorkspace}>
          <Stack gap="md">
            <Input
              label="Business / organization name"
              value={organizationName}
              onChange={(e) => setOrganizationName(e.target.value)}
            />
            <Button type="submit" loading={claim.isPending || finishing}>
              {finishing ? "Setting up your workspace…" : "Create your workspace"}
            </Button>
          </Stack>
        </form>
      </Card>
      <Button variant="outline" onClick={logOut}>
        Log out
      </Button>
    </Stack>
  );
}
