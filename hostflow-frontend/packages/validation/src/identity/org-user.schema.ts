import { z } from "zod";

// PLATFORM_ADMIN deliberately excluded — the self-service roles endpoint
// rejects it server-side; omitted here too so the UI never offers it.
export const selfServiceUserRoleSchema = z.enum(["XANUOS_OWNER", "XANUOS_MANAGER", "XANUOS_STAFF"]);

export const updateUserRolesFormSchema = z.object({
  roles: z.array(selfServiceUserRoleSchema).min(1, { message: "Select at least one role." }),
});

export type UpdateUserRolesFormValues = z.infer<typeof updateUserRolesFormSchema>;
