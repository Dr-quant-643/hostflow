"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  hostSignupFormSchema,
  type HostSignupFormValues,
} from "@hostflow/validation";
import { PageHeader, Stack, Card, Input, Button, toast } from "@hostflow/ui";
import { useRegisterHost } from "@hostflow/api-client/src/hooks/use-host-registration";
import { ApiError } from "@hostflow/api-client/src/errors";

export default function XanuosSignupPage() {
  const [registered, setRegistered] = useState(false);
  const register = useRegisterHost();
  const form = useForm<HostSignupFormValues>({
    resolver: zodResolver(hostSignupFormSchema),
    defaultValues: {
      organizationName: "",
      adminFirstName: "",
      adminLastName: "",
      adminEmail: "",
      password: "",
    },
  });

  if (registered) {
    return (
      <Stack gap="lg" className="mx-auto max-w-md p-6 text-center">
        <PageHeader
          title="Workspace created"
          description="Your XanuOS workspace is ready — log in to start setting up your properties."
        />
        <Button onClick={() => (window.location.href = "/xanuos/api/auth/login")}>
          Log In
        </Button>
      </Stack>
    );
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await register.mutateAsync(values);
      setRegistered(true);
      toast.success("Workspace created");
    } catch (err) {
      const message =
        err instanceof ApiError
          ? err.message
          : "Couldn't create your workspace. Please try again.";
      toast.error(message);
    }
  });

  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6">
      <PageHeader
        title="Set up your XanuOS workspace"
        description="Create your property management workspace in a minute"
      />
      <Card className="p-6">
        <Stack gap="md">
          <a
            href="/xanuos/api/auth/login?idp=google"
            className="flex items-center justify-center gap-2.5 rounded-lg border border-border bg-background px-4 py-2.5 text-sm font-medium text-foreground shadow-sm transition-colors hover:bg-muted"
          >
            <svg className="h-4 w-4" viewBox="0 0 48 48" aria-hidden="true">
              <path
                fill="#FFC107"
                d="M43.6 20.5H42V20H24v8h11.3C33.7 32.9 29.3 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.7-.4-3.5z"
              />
              <path
                fill="#FF3D00"
                d="M6.3 14.7l6.6 4.8C14.6 15.9 18.9 13 24 13c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4c-7.7 0-14.3 4.4-17.7 10.7z"
              />
              <path
                fill="#4CAF50"
                d="M24 44c5.5 0 10.4-1.9 14.3-5.1l-6.6-5.6C29.6 35 26.9 36 24 36c-5.3 0-9.7-3.1-11.3-7.9l-6.6 5.1C9.6 39.5 16.2 44 24 44z"
              />
              <path
                fill="#1976D2"
                d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.3 4.3-4.2 5.7l6.6 5.6C41 36.6 44 30.9 44 24c0-1.3-.1-2.7-.4-3.5z"
              />
            </svg>
            Continue with Google
          </a>

          <div className="flex items-center gap-3 text-xs text-muted-foreground">
            <div className="h-px flex-1 bg-border" />
            or set up with email
            <div className="h-px flex-1 bg-border" />
          </div>

          <p className="text-xs text-muted-foreground">
            Google sign-in is for logging back in once your workspace
            exists — to create a brand-new workspace, use the form below.
          </p>
        </Stack>

        <form onSubmit={onSubmit} className="mt-4">
          <Stack gap="md">
            <Input
              label="Business / organization name"
              {...form.register("organizationName")}
              error={form.formState.errors.organizationName?.message}
            />
            <Input
              label="First name"
              {...form.register("adminFirstName")}
              error={form.formState.errors.adminFirstName?.message}
            />
            <Input
              label="Last name"
              {...form.register("adminLastName")}
              error={form.formState.errors.adminLastName?.message}
            />
            <Input
              label="Email"
              {...form.register("adminEmail")}
              error={form.formState.errors.adminEmail?.message}
            />
            <Input
              label="Password"
              type="password"
              {...form.register("password")}
              error={form.formState.errors.password?.message}
            />
            <Button type="submit" loading={register.isPending}>
              Create workspace
            </Button>
          </Stack>
        </form>
      </Card>
    </Stack>
  );
}
