"use client";

import { Stack, PageHeader, Button } from "@hostflow/ui";

export default function AccessDeniedPage() {
  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6 text-center">
      <PageHeader
        title="Your workspace isn't set up yet"
        description="You signed in successfully, but your account isn't activated for XanuOS yet. If you're a property owner or manager interested in getting started, reach out to our team and we'll get your workspace ready."
      />
      <Button
        onClick={() => {
          // The route only accepts POST; a plain link would GET-navigate and 405.
          const form = document.createElement("form");
          form.method = "POST";
          form.action = "/xanuos/api/auth/logout";
          document.body.appendChild(form);
          form.submit();
        }}
      >
        Log out
      </Button>
    </Stack>
  );
}
