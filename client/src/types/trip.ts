export interface Trip {
  id: number;
  title: string;
  description: string;
  photos: string[] | null;
  tags: string[] | null;
  latitude: number | null;
  longitude: number | null;
  province: string | null;
  authorName?: string | null;
  address?: string | null;
}

/**
 * โครงสร้าง Page ที่ Spring Boot ส่งกลับมา
 */
export interface PagedTrips {
  content: Trip[];
  totalElements: number;
  totalPages: number;
  number: number; // page ปัจจุบัน (0-based)
  size: number;
  last: boolean;
}