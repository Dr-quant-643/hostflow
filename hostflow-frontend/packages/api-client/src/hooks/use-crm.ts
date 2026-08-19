import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../http-client";
import type { ContactResponse, InteractionResponse, ContactStatus } from "@hostflow/types";
import type { ContactFormValues } from "@hostflow/validation";

export function useContacts(status?: ContactStatus, limit = 20, offset = 0) {
  return useQuery({
    queryKey: ["crm", "contacts", "list", status ?? "all", limit, offset],
    queryFn: () =>
      api.get<ContactResponse[]>("/crm/contacts", { params: { status, limit, offset } }),
  });
}

export function useContact(id: string) {
  return useQuery({
    queryKey: ["crm", "contacts", "detail", id],
    queryFn: () => api.get<ContactResponse>(`/crm/contacts/${id}`),
    enabled: !!id,
  });
}

export function useContactInteractions(contactId: string) {
  return useQuery({
    queryKey: ["crm", "contacts", contactId, "interactions"],
    queryFn: () =>
      api.get<InteractionResponse[]>(`/crm/contacts/${contactId}/interactions`),
    enabled: !!contactId,
  });
}

export function useCreateContact() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: ContactFormValues) =>
      api.post<ContactResponse>("/crm/contacts", values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["crm", "contacts", "list"] });
    },
  });
}

export function useQualifyContact(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.patch<ContactResponse>(`/crm/contacts/${id}/qualify`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["crm", "contacts", "list"] });
      queryClient.invalidateQueries({ queryKey: ["crm", "contacts", "detail", id] });
    },
  });
}

export function useLogInteraction(contactId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (values: { type: string; notes?: string }) =>
      api.post<void>(`/crm/contacts/${contactId}/interactions`, values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["crm", "contacts", contactId, "interactions"] });
    },
  });
}
