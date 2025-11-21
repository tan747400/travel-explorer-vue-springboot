export interface Trip {
  id: number;
  title: string;

  description: string | null;      // รายละเอียดทริป

  photos: string[] | null;         // รูปภาพ (URL)
  tags: string[] | null;           // ["ทะเล", "ภูเขา"]

  latitude: number | null;         // พิกัดละติจูด
  longitude: number | null;        // พิกัดลองจิจูด

  province: string | null;         // จังหวัดของทริป
  authorName: string | null;       // ชื่อคนสร้างทริป

  // เผื่อใช้งานภายหลัง
  address?: string | null;
}

/** Pagination structure จาก Spring Boot */
export interface PagedTrips {
  content: Trip[];

  totalElements: number;
  totalPages: number;

  number: number;   // page index (0-based)
  size: number;     // items ต่อหน้า
  last: boolean;    // เป็นหน้าสุดท้ายไหม
}