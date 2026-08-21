"use client";

import { Stack, PageHeader, Button } from "@hostflow/ui";

export default function AccessDeniedPage() {
  return (
    <Stack gap="lg" className="p-6 text-center">
      <PageHeader
        title="Access denied"
        description="Your account doesn't have permission to use this app."
      />
      <Button
        onClick={() => {
          // The route only accepts POST; a plain link would GET-navigate and 405.
          const form = document.createElement("form");
          form.method = "POST";
          form.action = "/api/auth/logout";
          document.body.appendChild(form);
          form.submit();
        }}
      >
        Log out
      </Button>
    </Stack>
  );
}
