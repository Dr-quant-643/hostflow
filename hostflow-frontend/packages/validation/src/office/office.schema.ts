import { z } from "zod";
import { uuidSchema, nonEmptyString } from "../common";

export const meetingRoomFormSchema = z.object({
  propertyId: uuidSchema,
  name: nonEmptyString("Room name", 100),
  capacity: z.coerce.number().int().positive(),
});

export type MeetingRoomFormValues = z.infer<typeof meetingRoomFormSchema>;

// datetime-local input value ("YYYY-MM-DDTHH:mm") — converted to a full ISO
// instant at the API call boundary, not here.
export const roomBookingFormSchema = z
  .object({
    roomId: uuidSchema,
    startsAt: nonEmptyString("Start time"),
    endsAt: nonEmptyString("End time"),
    purpose: z.string().max(200).optional(),
  })
  .refine((data) => data.endsAt > data.startsAt, {
    message: "End time must be after start time.",
    path: ["endsAt"],
  });

export type RoomBookingFormValues = z.infer<typeof roomBookingFormSchema>;

export const visitorFormSchema = z.object({
  propertyId: uuidSchema,
  fullName: nonEmptyString("Full name", 150),
  company: z.string().max(150).optional(),
  expectedAt: nonEmptyString("Expected time"),
});

export type VisitorFormValues = z.infer<typeof visitorFormSchema>;
