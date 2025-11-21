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

/** ดึงทริปของ user ปัจจุบัน (ใช้ใน Dashboard) */
export async function getMyTrips(token: string): Promise<Trip[]> {
  const res = await fetch(`${API_BASE}/trips/mine`, {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.status === 401) {
    throw new Error("กรุณาเข้าสู่ระบบก่อนเข้าหน้านี้");
  }

  if (!res.ok) {
    throw new Error(`โหลดทริปไม่สำเร็จ (HTTP ${res.status})`);
  }

  return (await res.json()) as Trip[];
}

/** payload ที่ใช้ทั้ง create/update */
export interface TripPayload {
  title: string;
  province: string;
  description: string | null;
  tags: string[] | null;
  latitude: number | null;
  longitude: number | null;
}

/** สร้างทริปใหม่ */
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

  const text = await res.text();
  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    // response ไม่ใช่ JSON ก็ข้ามไป
  }

  if (!res.ok) {
    const backendMsg = data?.message || data?.error;
    const msg =
      backendMsg || `บันทึกทริปไม่สำเร็จ (HTTP ${res.status})`;
    throw new Error(msg);
  }

  return data as Trip;
}

/** อัปเดตทริป */
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
      backendMsg || `บันทึกการแก้ไขไม่สำเร็จ (HTTP ${res.status})`;
    throw new Error(msg);
  }

  return data as Trip;
}

/** ลบทริป */
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