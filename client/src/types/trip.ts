export interface Trip {
    id: number;
    title: string;
    description: string;
    photos: string[];    // ใช้รูปแรกเป็น cover
    tags: string[];
    latitude?: number | null;
    longitude?: number | null;
    province?: string | null;
    url?: string | null; // ถ้าฝั่ง backend มีฟิลด์นี้ค่อยเติมจริงทีหลัง
    authorName?: string | null;
  }