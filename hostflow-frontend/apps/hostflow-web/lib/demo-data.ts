import type { PublicPropertySummary } from "@hostflow/types";

// Demo-only content for a populated, screenshot-ready nazilco-web preview
// while the backend/Docker stack isn't running. Shapes match the real
// PublicPropertySummary/GuestInvoiceRow/etc. contracts exactly. Kenya-weighted
// lineup (Maasai Mara, Nairobi x2, Diani Beach) with a few international
// stays for variety.

export interface DemoProperty extends PublicPropertySummary {
  photos: string[];
  rating: number;
  reviewCount: number;
  highlights: string[];
  bedrooms: number;
  bathrooms: number;
  maxGuests: number;
  hostName: string;
}

const img = (id: string) =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=1200&q=80`;

export const DEMO_PROPERTIES: DemoProperty[] = [
  {
    id: "d1000000-0000-0000-0000-000000000001",
    name: "Azure Cliffside Villa",
    description:
      "Perched above the coastline with uninterrupted ocean views, this villa pairs sun-bleached stone with floor-to-ceiling glass. Wake up to the sound of waves and take breakfast on the infinity-edge terrace.",
    propertyType: "VACATION_RENTAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "14 Marina Point Road",
    city: "Santorini",
    country: "Greece",
    latitude: 36.3932,
    longitude: 25.4615,
    basePrice: "54000.00",
    photos: [
      img("photo-1600596542815-ffad4c1539a9"),
      img("photo-1600585154340-be6161a56a0c"),
      img("photo-1600607687939-ce8a6c25118c"),
      img("photo-1512917774080-9991f1c4c750"),
      img("photo-1584622650111-993a426fbf0a"),
    ],
    rating: 4.96,
    reviewCount: 214,
    highlights: ["Infinity pool", "Ocean-view terrace", "Private chef available", "Free parking"],
    bedrooms: 4,
    bathrooms: 3,
    maxGuests: 8,
    hostName: "Elena",
  },
  {
    id: "d1000000-0000-0000-0000-000000000002",
    name: "Maasai Mara Safari Camp",
    description:
      "A luxury tented camp on the edge of the Maasai Mara, where the sound of the savanna replaces any alarm clock. Canvas suites with private decks, a plunge pool, and front-row seats to the Great Migration.",
    propertyType: "VACATION_RENTAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "Mara Triangle Conservancy Road",
    city: "Narok",
    country: "Kenya",
    latitude: -1.4061,
    longitude: 35.0147,
    basePrice: "42000.00",
    photos: [
      img("photo-1516426122078-c23e76319801"),
      img("photo-1516130205964-2d9dfc7c4c46"),
      img("photo-1523805009345-7448845a9e53"),
      img("photo-1547471080-7cc2caa01a7e"),
    ],
    rating: 4.93,
    reviewCount: 168,
    highlights: ["Twice-daily game drives", "Private plunge pool", "Fire-lit bush dinners", "Maasai village visits"],
    bedrooms: 3,
    bathrooms: 3,
    maxGuests: 6,
    hostName: "Wanjiru",
  },
  {
    id: "d1000000-0000-0000-0000-000000000003",
    name: "Kilimani Sky Residence",
    description:
      "A designer apartment on the 18th floor overlooking Nairobi's skyline and the Ngong Hills beyond. Walk to Yaya Centre and Kilimani's best cafés, or unwind by the rooftop pool at sunset.",
    propertyType: "RESIDENTIAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "12 Wood Avenue, Kilimani",
    city: "Nairobi",
    country: "Kenya",
    latitude: -1.2954,
    longitude: 36.7870,
    basePrice: "13500.00",
    photos: [
      img("photo-1560185893-a55cbc8c57e8"),
      img("photo-1502005229762-cf1b2da7c5d6"),
      img("photo-1522708323590-d24dbb6b0267"),
      img("photo-1493809842364-78817add7ffb"),
    ],
    rating: 4.85,
    reviewCount: 226,
    highlights: ["Rooftop pool & gym", "Ngong Hills views", "Walk to Yaya Centre", "Backup power & fast wifi"],
    bedrooms: 2,
    bathrooms: 2,
    maxGuests: 4,
    hostName: "Njoroge",
  },
  {
    id: "d1000000-0000-0000-0000-000000000004",
    name: "Casa de Bugambilia",
    description:
      "A hacienda-style courtyard home draped in bougainvillea, with hand-painted tile, a plunge pool, and a rooftop dining terrace looking over the old town.",
    propertyType: "VACATION_RENTAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "22 Calle de las Flores",
    city: "Oaxaca",
    country: "Mexico",
    latitude: 17.0732,
    longitude: -96.7266,
    basePrice: "23000.00",
    photos: [
      img("photo-1580587771525-78b9dba3b914"),
      img("photo-1484154218962-a197022b5858"),
      img("photo-1523217582562-09d0def993a6"),
      img("photo-1613977257363-707ba9348227"),
    ],
    rating: 4.91,
    reviewCount: 98,
    highlights: ["Plunge pool", "Rooftop terrace", "Walk to town square", "Breakfast included"],
    bedrooms: 3,
    bathrooms: 2,
    maxGuests: 6,
    hostName: "Renata",
  },
  {
    id: "d1000000-0000-0000-0000-000000000005",
    name: "Diani Beach Villa",
    description:
      "Barefoot luxury steps from the white sand of Diani. Palm-shaded terraces, an outdoor rain shower, and a rooftop deck built for long sundowners over the Indian Ocean.",
    propertyType: "VACATION_RENTAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "7 Beach Road, Diani",
    city: "Diani Beach",
    country: "Kenya",
    latitude: -4.2761,
    longitude: 39.5908,
    basePrice: "28500.00",
    photos: [
      img("photo-1519046904884-53103b34b206"),
      img("photo-1544551763-46a013bb70d5"),
      img("photo-1507525428034-b723cf961d3e"),
      img("photo-1499793983690-e29da59ef1c2"),
    ],
    rating: 4.94,
    reviewCount: 187,
    highlights: ["Steps to the beach", "Private dhow sailing trips", "Coral reef snorkeling", "Rooftop sundowner deck"],
    bedrooms: 4,
    bathrooms: 3,
    maxGuests: 9,
    hostName: "Fatuma",
  },
  {
    id: "d1000000-0000-0000-0000-000000000006",
    name: "Kyoto Machiya House",
    description:
      "A restored 1920s machiya townhouse with a private tsubo-niwa garden, tatami rooms, and a modern cedar soaking tub. A five-minute walk to Gion.",
    propertyType: "VACATION_RENTAL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "3 Higashiyama Lane",
    city: "Kyoto",
    country: "Japan",
    latitude: 35.0116,
    longitude: 135.7681,
    basePrice: "31500.00",
    photos: [
      img("photo-1600664356215-9c611f5b4d80"),
      img("photo-1578683010236-d716f9a3f461"),
      img("photo-1600566752355-35792bedcfea"),
      img("photo-1615874959474-d609969a20ed"),
    ],
    rating: 4.98,
    reviewCount: 132,
    highlights: ["Private garden", "Cedar soaking tub", "5 min to Gion", "Tea ceremony set"],
    bedrooms: 2,
    bathrooms: 1,
    maxGuests: 4,
    hostName: "Haruto",
  },
  {
    id: "d1000000-0000-0000-0000-000000000007",
    name: "The Acacia Nairobi Hotel",
    description:
      "A boutique hotel in the heart of Westlands, built around a courtyard of acacia trees. Rooftop infinity pool, an all-day restaurant, and easy access to Nairobi's business and nightlife districts.",
    propertyType: "HOTEL",
    rentalModel: "NIGHTLY",
    manualOccupiedUntil: null,
    addressLine: "45 Waiyaki Way, Westlands",
    city: "Nairobi",
    country: "Kenya",
    latitude: -1.2667,
    longitude: 36.8038,
    basePrice: "18500.00",
    photos: [
      img("photo-1611892440504-42a792e24d32"),
      img("photo-1590490360182-c33d57733427"),
      img("photo-1571003123894-1f0594d2b5d9"),
      img("photo-1611048268330-53de574cae3b"),
    ],
    rating: 4.78,
    reviewCount: 302,
    highlights: ["Rooftop infinity pool", "Airport shuttle", "On-site spa", "Walk to Westlands nightlife"],
    bedrooms: 1,
    bathrooms: 1,
    maxGuests: 2,
    hostName: "The Acacia Team",
  },
];

export interface DemoReview {
  id: string;
  guestName: string;
  rating: number;
  comment: string;
  stayedAt: string;
  avatarSeed: string;
}

export const DEMO_REVIEWS: Record<string, DemoReview[]> = {
  "d1000000-0000-0000-0000-000000000001": [
    { id: "r1", guestName: "Isabelle", rating: 5, comment: "The view from the terrace alone is worth the trip. Elena was an incredible host — thank you!", stayedAt: "July 2026", avatarSeed: "Isabelle" },
    { id: "r2", guestName: "Tom", rating: 5, comment: "Photos genuinely do not do this place justice. We didn't want to leave.", stayedAt: "June 2026", avatarSeed: "Tom" },
    { id: "r3", guestName: "Naomi", rating: 4, comment: "Beautiful villa, a bit of a walk from town but worth it for the privacy.", stayedAt: "May 2026", avatarSeed: "Naomi" },
  ],
  "d1000000-0000-0000-0000-000000000002": [
    { id: "r4", guestName: "Derek", rating: 5, comment: "Woke up to a herd of elephants outside camp on our second morning. Wanjiru and the team made it unforgettable.", stayedAt: "August 2026", avatarSeed: "Derek" },
    { id: "r5", guestName: "Achieng", rating: 5, comment: "The bush dinner under the stars was the highlight of our whole trip to Kenya.", stayedAt: "July 2026", avatarSeed: "Achieng" },
  ],
  "d1000000-0000-0000-0000-000000000003": [
    { id: "r6", guestName: "James", rating: 5, comment: "Unbeatable views of the Ngong Hills at sunset. Super central for Kilimani and easy Uber access.", stayedAt: "June 2026", avatarSeed: "James" },
    { id: "r7", guestName: "Wambui", rating: 4, comment: "Stylish apartment, rooftop pool was a nice touch after a long day in the city.", stayedAt: "May 2026", avatarSeed: "Wambui" },
  ],
  "d1000000-0000-0000-0000-000000000004": [
    { id: "r8", guestName: "Lucia", rating: 5, comment: "Waking up to breakfast on the rooftop every morning — pure magic.", stayedAt: "April 2026", avatarSeed: "Lucia" },
  ],
  "d1000000-0000-0000-0000-000000000005": [
    { id: "r9", guestName: "Chris", rating: 5, comment: "The dhow sunset sail Fatuma arranged was the best evening of our whole Kenya coast trip.", stayedAt: "July 2026", avatarSeed: "Chris" },
    { id: "r10", guestName: "Amina", rating: 5, comment: "Immaculately kept villa, steps from the water, and the snorkeling nearby is unreal.", stayedAt: "June 2026", avatarSeed: "Amina" },
  ],
  "d1000000-0000-0000-0000-000000000006": [
    { id: "r11", guestName: "Kenji", rating: 5, comment: "An incredibly peaceful stay. The garden at night is unforgettable.", stayedAt: "March 2026", avatarSeed: "Kenji" },
  ],
  "d1000000-0000-0000-0000-000000000007": [
    { id: "r12", guestName: "Brian", rating: 5, comment: "Great base for meetings in Westlands, and the rooftop pool is a lovely way to unwind after.", stayedAt: "August 2026", avatarSeed: "Brian" },
    { id: "r13", guestName: "Zawadi", rating: 4, comment: "Clean, central, friendly staff. Breakfast spread was excellent.", stayedAt: "June 2026", avatarSeed: "Zawadi" },
  ],
};

export function demoAvatarUrl(seed: string): string {
  return `https://api.dicebear.com/7.x/notionists/svg?seed=${encodeURIComponent(seed)}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf`;
}

export function findDemoProperty(id: string): DemoProperty | undefined {
  return DEMO_PROPERTIES.find((p) => p.id === id);
}
