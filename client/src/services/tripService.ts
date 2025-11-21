import type { Trip, PagedTrips } from "@/types/trip";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const API_BASE = `${API_BASE_URL}/api`;

/** ดึง list trips แบบมี pagination */
export async function getTrips(
  keyword = "",
  page = 0,
  size = 6
): Promise<PagedTrips> {
  const params = new URLSearchParams();

  if (keyword.trim()) {
    params.set("keyword", keyword.trim());
  }

  params.set("page", String(page));
  params.set("size", String(size));

  const url = `${API_BASE}/trips?${params.toString()}`;

  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Failed to fetch trips: ${res.status} ${res.statusText}`);
  }

  return (await res.json()) as PagedTrips;
}

/** ดึงทริปตัวเดียวตาม id */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch trip id=${id}: ${res.status}`);
  }
  return (await res.json()) as Trip;
}

/** สร้างทริปใหม่ (ต้องมี token) */
export interface TripPayload {
  title: string;
  province: string;
  description: string | null;
  tags: string[] | null;
  latitude: number | null;
  longitude: number | null;
}

export async function createTrip(
  token: string,
  payload: TripPayload
): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (res.status === 401) {
    throw new Error("เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง (401)");
  }

  // พยายามอ่าน response เพื่อช่วย debug
  const text = await res.text();
  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    // ไม่ใช่ JSON ก็ไม่เป็นไร
  }

  if (!res.ok) {
    // ถ้า backend ส่ง message มาก็เอามาใช้
    const backendMsg = data?.message || data?.error;
    const msg =
      backendMsg ||
      `บันทึกทริปไม่สำเร็จ (HTTP ${res.status})`; // จะเห็น status ชัด ๆ
    throw new Error(msg);
  }

  return data as Trip;
}

/** อัปเดตทริป (PUT /api/trips/{id}) */
export async function updateTrip(
  id: number,
  token: string,
  payload: TripPayload
): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (res.status === 401) {
    throw new Error("เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
  }
  if (res.status === 403) {
    throw new Error("คุณไม่มีสิทธิ์แก้ไขทริปนี้");
  }

  const text = await res.text();
  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {}

  if (!res.ok) {
    const backendMsg = data?.message || data?.error;
    const msg =
      backendMsg ||
      `บันทึกการแก้ไขไม่สำเร็จ (HTTP ${res.status})`;
    throw new Error(msg);
  }

  return data as Trip;
}

/** ลบทริปตาม id (ต้องส่ง token มาด้วย) */
export async function deleteTrip(id: number, token: string): Promise<void> {
  const res = await fetch(`${API_BASE}/trips/${id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.status === 401) {
    throw new Error("กรุณาเข้าสู่ระบบใหม่อีกครั้ง (401)");
  }

  if (res.status === 403) {
    throw new Error("คุณไม่มีสิทธิ์ลบทริปนี้ (403)");
  }

  if (res.status === 404) {
    throw new Error("ไม่พบทริปที่ต้องการลบ (404)");
  }

  if (!res.ok) {
    throw new Error(`ลบทริปไม่สำเร็จ (HTTP ${res.status})`);
  }
}