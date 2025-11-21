import type { Trip, PagedTrips } from "@/types/trip";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const API = `${API_BASE_URL}/api/trips`;

/* ======================================================
   GET: ดึงรายการ trips แบบมี pagination
====================================================== */
export async function getTrips(
  keyword = "",
  page = 0,
  size = 6
): Promise<PagedTrips> {
  const params = new URLSearchParams();

  if (keyword.trim()) params.set("keyword", keyword.trim());
  params.set("page", String(page));
  params.set("size", String(size));

  const res = await fetch(`${API}?${params.toString()}`);

  if (!res.ok) {
    throw new Error(`โหลดรายการทริปไม่สำเร็จ (${res.status})`);
  }

  return (await res.json()) as PagedTrips;
}

/* ======================================================
   GET: ดึงทริปตาม ID
====================================================== */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API}/${id}`);

  if (!res.ok) throw new Error(`ไม่พบทริปนี้ (${res.status})`);

  return (await res.json()) as Trip;
}

/* ======================================================
   DELETE: ลบทริป
====================================================== */
export async function deleteTrip(id: number, token: string): Promise<void> {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.status === 401) throw new Error("กรุณาเข้าสู่ระบบอีกครั้ง (401)");
  if (res.status === 403) throw new Error("คุณไม่มีสิทธิ์ลบทริปนี้ (403)");
  if (res.status === 404) throw new Error("ไม่พบทริปนี้ (404)");
  if (!res.ok) throw new Error("ลบทริปไม่สำเร็จ");
}

/* ======================================================
   PUT: อัปเดตทริป
====================================================== */
export async function updateTrip(
  id: number,
  token: string,
  payload: {
    title: string;
    province: string;
    description: string | null;
    tags: string[] | null;
    latitude: number | null;
    longitude: number | null;
  }
): Promise<Trip> {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (res.status === 401) throw new Error("กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
  if (res.status === 403) throw new Error("คุณไม่มีสิทธิ์แก้ไขทริปนี้");

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.message || "อัปเดตทริปไม่สำเร็จ");
  }

  return (await res.json()) as Trip;
}