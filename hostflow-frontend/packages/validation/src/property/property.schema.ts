import { z } from "zod";
import { nonEmptyString } from "../common";

// Mirrors module-property's PropertyType enum exactly.
export const propertyTypeSchema = z.enum([
  "RESIDENTIAL",
  "HOTEL",
  "VACATION_RENTAL",
  "OFFICE",
  "RETAIL_MALL",
  "MIXED_USE",
]);

export const propertyFormSchema = z.object({
  name: nonEmptyString("Property name", 150),
  propertyType: propertyTypeSchema,
  addressLine: nonEmptyString("Address", 200),
  city: nonEmptyString("City", 100),
  country: nonEmptyString("Country", 100),
});

export type PropertyFormValues = z.infer<typeof propertyFormSchema>;
