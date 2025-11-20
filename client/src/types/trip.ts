export interface Trip {
  id: number;
  title: string;
  description: string | null;      // รายละเอียดทริป 

  photos: string[] | null;         // รูปภาพ (ตอนนี้ยังไม่ใช้ แต่รองรับตาม backend)
  tags: string[] | null;           // เช่น ["ทะเล", "ภูเขา"]

  latitude: number | null;         // พิกัดละติจูด
  longitude: number | null;        // พิกัดลองจิจูด

  province: string | null;         // จังหวัดของทริป
  authorName: string | null;       // ชื่อคนสร้างทริป (Dashboard / Detail ใช้)

  // เผื่อ backend เพิ่ม address ในอนาคต
  address?: string | null;
}

/**
 * โครงสร้าง Pagination ที่ Spring Boot ส่งกลับมาเวลาโหลด Trips แบบค้นหา + หน้า Landing
 */
export interface PagedTrips {
  content: Trip[];
  totalElements: number;
  totalPages: number;

  number: number;   // page ปัจจุบัน (0-based)
  size: number;     // จำนวนข้อมูลต่อหน้า เช่น 6
  last: boolean;    // true = เป็นหน้าสุดท้าย
}