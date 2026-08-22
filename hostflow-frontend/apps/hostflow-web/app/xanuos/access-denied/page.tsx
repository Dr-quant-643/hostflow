"use client";

import Link from "next/link";
import { Stack, PageHeader, Button } from "@hostflow/ui";

export default function AccessDeniedPage() {
  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6 text-center">
      <PageHeader
        title="No workspace linked to this account"
        description="You signed in successfully, but this account isn't attached to a XanuOS workspace yet. If you're a property owner or manager, create your own workspace below — or log out and sign in with the account your team invited you on."
      />
      <Stack gap="sm">
        <Button asChild>
          <Link href="/xanuos/signup">Create your workspace</Link>
        </Button>
        <Button
          variant="outline"
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
    </Stack>
  );
}
