import { z } from 'zod'

// Define a schema for a string that replaces null with an empty string
const nullableString = z.preprocess((val) => (val === null ? "" : val), z.string());

// Define the main schema, using nullableString where needed
export const positionSchema = z.object({
  id: z.number(),
  deviceid: z.number(),
  longitude: z.number(),
  protocol: nullableString,  // Convert null to empty string
  devicename: nullableString,  // Convert null to empty string
  latitude: z.number(),
  devicetime: nullableString,  // Convert null to empty string
});

export type Position = z.infer<typeof positionSchema>;
