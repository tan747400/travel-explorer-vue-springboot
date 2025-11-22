import type { Trip, PagedTrips } from "@/types/trip";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const API_BASE = `${API_BASE_URL}/api`;

/** ดึง Trips แบบ Pagination + Search */
export async function getTrips(
  keyword = "",
  page = 0,
  size = 6
): Promise<PagedTrips> {
  const params = new URLSearchParams();

  if (keyword.trim()) params.set("keyword", keyword.trim());
  params.set("page", String(page));
  params.set("size", String(size));

  const res = await fetch(`${API_BASE}/trips?${params.toString()}`);

  if (!res.ok) {
    const err: any = new Error(`โหลดทริปไม่สำเร็จ (${res.status})`);
    err.status = res.status;
    throw err;
  }

  return (await res.json()) as PagedTrips;
}

/** ดึงทริปเดี่ยว */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`);

  if (!res.ok) {
    const err: any = new Error(`ไม่พบข้อมูลทริป (${res.status})`);
    err.status = res.status;
    throw err;
  }

  return (await res.json()) as Trip;
}

/** ดึงทริปของ user ปัจจุบัน */
export async function getMyTrips(token: string): Promise<Trip[]> {
  const res = await fetch(`${API_BASE}/trips/mine`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  const err: any = new Error("โหลดทริปไม่สำเร็จ");
  err.status = res.status;

  if (res.status === 401) throw err;
  if (!res.ok) throw err;

  return (await res.json()) as Trip[];
}

/** Payload */
export interface TripPayload {
  title: string;
  province: string;
  description: string | null;
  tags: string[] | null;
  latitude: number | null;
  longitude: number | null;
}

/** สร้างทริป */
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

  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {}

  if (!res.ok) {
    const err: any = new Error(data?.message || "บันทึกทริปไม่สำเร็จ");
    err.status = res.status;
    throw err;
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

  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {}

  if (!res.ok) {
    const err: any = new Error(data?.message || "แก้ไขทริปไม่สำเร็จ");
    err.status = res.status;
    throw err;
  }

  return data as Trip;
}

/** ลบทริป */
export async function deleteTrip(id: number, token: string): Promise<void> {
  const res = await fetch(`${API_BASE}/trips/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });

  const err: any = new Error("ลบทริปไม่สำเร็จ");
  err.status = res.status;

  if (res.status === 401) throw err;
  if (res.status === 403) throw err;
  if (res.status === 404) throw err;

  if (!res.ok) throw err;
}