import type { Trip, PagedTrips } from "@/types/trip";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const API_BASE = `${API_BASE_URL}/api`;

// แนบ status ลง error
function buildError(message: string, status?: number): Error {
  const err: any = new Error(message);
  if (status) err.status = status;
  return err;
}

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
    throw buildError(`โหลดทริปไม่สำเร็จ (${res.status})`, res.status);
  }

  return (await res.json()) as PagedTrips;
}

/** ดึงทริปเดี่ยว */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`);
  if (!res.ok) throw buildError(`ไม่พบข้อมูลทริป (${res.status})`, res.status);
  return (await res.json()) as Trip;
}

/** ดึงทริปของ user ปัจจุบัน */
export async function getMyTrips(token: string): Promise<Trip[]> {
  const res = await fetch(`${API_BASE}/trips/mine`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (res.status === 401) {
    // ให้ Dashboard รู้ว่า token หมดอายุ
    throw buildError(
      "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง",
      401
    );
  }

  if (!res.ok) {
    throw buildError("โหลดทริปไม่สำเร็จ", res.status);
  }

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
  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    // ignore
  }

  if (res.status === 401) {
    throw buildError(
      "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง",
      401
    );
  }

  if (!res.ok) {
    const msg = data?.message || data?.error || "บันทึกทริปไม่สำเร็จ";
    throw buildError(msg, res.status);
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
  let data: any = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    // ignore
  }

  if (res.status === 401) {
    throw buildError(
      "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง",
      401
    );
  }

  if (!res.ok) {
    const msg = data?.message || data?.error || "แก้ไขทริปไม่สำเร็จ";
    throw buildError(msg, res.status);
  }

  return data as Trip;
}

/** ลบทริป */
export async function deleteTrip(id: number, token: string): Promise<void> {
  const res = await fetch(`${API_BASE}/trips/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (res.status === 401) {
    throw buildError(
      "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง",
      401
    );
  }

  if (res.status === 403) {
    throw buildError("คุณไม่มีสิทธิ์ลบทริปนี้", 403);
  }
  if (res.status === 404) {
    throw buildError("ไม่พบทริป", 404);
  }

  if (!res.ok) throw buildError("ลบทริปไม่สำเร็จ", res.status);
}