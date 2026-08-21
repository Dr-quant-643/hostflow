"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  guestSignupFormSchema,
  type GuestSignupFormValues,
} from "@hostflow/validation";
import { PageHeader, Stack, Card, Input, Button, toast } from "@hostflow/ui";
import { useRegisterGuest } from "@hostflow/api-client/src/hooks/use-guest-registration";
import { ApiError } from "@hostflow/api-client/src/errors";

export default function SignupPage() {
  const [registered, setRegistered] = useState(false);
  const register = useRegisterGuest();
  const form = useForm<GuestSignupFormValues>({
    resolver: zodResolver(guestSignupFormSchema),
    defaultValues: { firstName: "", lastName: "", email: "", phone: "", password: "" },
  });

  if (registered) {
    return (
      <Stack gap="lg" className="p-6 text-center">
        <PageHeader title="Account created" description="You can now log in." />
        <Button onClick={() => (window.location.href = "/nazilco/api/auth/login")}>
          Log In
        </Button>
      </Stack>
    );
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      await register.mutateAsync(values);
      setRegistered(true);
      toast.success("Account created");
    } catch (err) {
      const message =
        err instanceof ApiError
          ? err.message
          : "Couldn't create your account. Please try again.";
      toast.error(message);
    }
  });

  return (
    <Stack gap="lg" className="mx-auto max-w-md p-6">
      <PageHeader title="Create your account" description="Sign up to book properties on NazilCo" />
      <Card className="p-6">
        <form onSubmit={onSubmit}>
          <Stack gap="md">
            <Input
              label="First Name"
              {...form.register("firstName")}
              error={form.formState.errors.firstName?.message}
            />
            <Input
              label="Last Name"
              {...form.register("lastName")}
              error={form.formState.errors.lastName?.message}
            />
            <Input
              label="Email"
              {...form.register("email")}
              error={form.formState.errors.email?.message}
            />
            <Input
              label="Phone (optional)"
              {...form.register("phone")}
              error={form.formState.errors.phone?.message}
            />
            <Input
              label="Password"
              type="password"
              {...form.register("password")}
              error={form.formState.errors.password?.message}
            />
            <Button type="submit" loading={register.isPending}>
              Sign Up
            </Button>
          </Stack>
        </form>
      </Card>
    </Stack>
  );
}
