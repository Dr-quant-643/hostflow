"use client";

import { useState } from "react";
import { Stack, PageHeader, Button, toast } from "@hostflow/ui";
import { useClaimGuestProfile } from "@hostflow/api-client/src/hooks/use-claim-guest-profile";
import { ApiError } from "@hostflow/api-client/src/errors";

export default function AccessDeniedPage() {
  const [finishing, setFinishing] = useState(false);
  const claim = useClaimGuestProfile();

  const logOut = () => {
    // The route only accepts POST; a plain link would GET-navigate and 405.
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/nazilco/api/auth/logout";
    document.body.appendChild(form);
    form.submit();
  };

  const onActivate = async () => {
    try {
      await claim.mutateAsync();
      // The session cookie still carries the pre-profile claims -- refresh
      // it so PRODUCT_NAZILCO actually takes effect before we navigate on.
      setFinishing(true);
      await fetch("/nazilco/api/auth/refresh", { method: "POST" });
      window.location.href = "/nazilco";
    } catch (err) {
      const message =
        err instanceof ApiError ? err.message : "Couldn't activate your profile. Please try again.";
      toast.error(message);
    }
  };

  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6 text-center">
      <PageHeader
        title="Your NazilCo profile isn't set up yet"
        description="You signed in successfully, but this account isn't activated as a NazilCo guest yet. Activate it below to start browsing, saving, and booking stays."
      />
      <Stack gap="sm">
        <Button onClick={onActivate} loading={claim.isPending || finishing}>
          {finishing ? "Setting things up…" : "Activate my NazilCo profile"}
        </Button>
        <Button variant="outline" onClick={logOut}>
          Log out
        </Button>
      </Stack>
    </Stack>
  );
}
