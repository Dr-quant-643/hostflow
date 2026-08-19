// Mirrors module-analytics's PropertyOccupancyResponse / MonthlyRevenueResponse.

export interface PropertyOccupancyResponse {
  propertyId: string;
  propertyName: string;
  totalBookings: number;
  totalNightsBooked: number;
  totalRevenue: string; // BigDecimal as string
}

export interface MonthlyRevenueResponse {
  month: string; // ISO "YYYY-MM"
  invoicedTotal: string;
  paidTotal: string;
  invoiceCount: number;
}
