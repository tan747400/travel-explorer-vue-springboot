// src/services/tripService.ts
import type { Trip } from "../types/trip";

/**
 * base URL ของ backend
 * เวลา dev ให้ตั้งในไฟล์ .env.local เช่น
 *   VITE_API_BASE_URL=http://localhost:8080
 */
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

/**
 * shape ของข้อมูลที่ backend ส่งมา (DTO จาก Spring Boot)
 * ให้ตั้งชื่อให้ตรงกับ JSON response จริงของ /api/trips
 */
interface TripApiResponse {
  id: number;
  title: string;
  description: string | null;
  photos: string[];
  tags: string[];
  latitude: number | null;
  longitude: number | null;
  authorId?: number | null;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * map จาก TripApiResponse -> Trip ที่ฝั่ง frontend ใช้งาน
 * (ถ้าใน type Trip มี field province / url ก็ให้เป็น optional)
 */
function mapTripFromApi(apiTrip: TripApiResponse): Trip {
  return {
    id: apiTrip.id,
    title: apiTrip.title,
    description: apiTrip.description ?? "",
    photos: apiTrip.photos ?? [],
    tags: apiTrip.tags ?? [],
    latitude: apiTrip.latitude ?? 0,
    longitude: apiTrip.longitude ?? 0,

    // ถ้าใน Trip ไม่มีสองอันนี้ก็ลบทิ้งได้
    province: undefined,
    url: undefined,
  };
}

/**
 * ดึงลิสต์ trips ทั้งหมด (รองรับ keyword จาก query param)
 * ส่งไปที่ GET /api/trips?keyword=...
 */
export async function getTrips(keyword = ""): Promise<Trip[]> {
  const params = new URLSearchParams();
  const kw = keyword.trim();
  if (kw) params.set("keyword", kw);

  const url =
    params.toString().length > 0
      ? `${API_BASE_URL}/api/trips?${params.toString()}`
      : `${API_BASE_URL}/api/trips`;

  const res = await fetch(url);

  if (!res.ok) {
    // ถ้าอยาก fallback กลับไปใช้ mock ก็ทำตรงนี้ได้
    throw new Error(`Failed to fetch trips: ${res.status} ${res.statusText}`);
  }

  const data: TripApiResponse[] = await res.json();
  return data.map(mapTripFromApi);
}

/**
 * ดึง trip ตาม id
 * GET /api/trips/{id}
 */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE_URL}/api/trips/${id}`);

  if (res.status === 404) {
    throw new Error("Trip not found");
  }

  if (!res.ok) {
    throw new Error(`Failed to fetch trip: ${res.status} ${res.statusText}`);
  }

  const data: TripApiResponse = await res.json();
  return mapTripFromApi(data);
}