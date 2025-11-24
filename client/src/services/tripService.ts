import type { Trip, PagedTrips } from "@/types/trip";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const API_BASE = `${API_BASE_URL}/api`;

// =========================
// Helpers
// =========================

// แนบ status ลง error
function buildError(message: string, status?: number): Error {
  const err: any = new Error(message);
  if (status) err.status = status;
  return err;
}

// parse JSON อย่างปลอดภัย (รองรับ body ว่าง ๆ)
async function parseJsonSafe(res: Response): Promise<any | null> {
  const text = await res.text();
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

// ใช้สำหรับ request ที่ต้อง auth
async function handleAuthJson<T>(
  res: Response,
  defaultErrorMessage: string
): Promise<T> {
  const data = await parseJsonSafe(res);

  if (res.status === 401) {
    // token หาย / หมดอายุ
    throw buildError(
      "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง",
      401
    );
  }

  if (res.status === 403) {
    const msg =
      data?.message ||
      data?.error ||
      "คุณไม่มีสิทธิ์ดำเนินการกับทริปนี้";
    throw buildError(msg, 403);
  }

  if (!res.ok) {
    const msg = data?.message || data?.error || defaultErrorMessage;
    throw buildError(msg, res.status);
  }

  return data as T;
}

// =========================
// Meta type
// =========================

// ใช้กับ endpoint /api/trips/meta
export interface TripsMeta {
  provinces: string[];
  tags: string[];
}

// =========================
// Public endpoints (ไม่ต้อง auth)
// =========================

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

/** ดึง meta (province + tags ทั้งหมดจากทุกทริป) */
export async function getTripsMeta(): Promise<TripsMeta> {
  const res = await fetch(`${API_BASE}/trips/meta`);

  if (!res.ok) {
    throw buildError(
      `โหลดข้อมูลตัวกรองไม่สำเร็จ (${res.status})`,
      res.status
    );
  }

  return (await res.json()) as TripsMeta;
}

/** ดึงทริปเดี่ยว */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`);

  if (res.status === 404) {
    throw buildError("ไม่พบข้อมูลทริป", 404);
  }

  if (!res.ok) {
    throw buildError(`ไม่พบข้อมูลทริป (${res.status})`, res.status);
  }

  return (await res.json()) as Trip;
}

// =========================
// Protected endpoints (ต้อง auth)
// =========================

/** ดึงทริปของ user ปัจจุบัน */
export async function getMyTrips(token: string): Promise<Trip[]> {
  const res = await fetch(`${API_BASE}/trips/mine`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return handleAuthJson<Trip[]>(res, "โหลดทริปของฉันไม่สำเร็จ");
}

/** Payload สำหรับ create / update */
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

  // ถ้า backend ส่ง message มาพิเศษ (เช่น duplicate, validation) จะถูกดึงใช้ใน handleAuthJson
  return handleAuthJson<Trip>(res, "บันทึกทริปไม่สำเร็จ");
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

  return handleAuthJson<Trip>(res, "แก้ไขทริปไม่สำเร็จ");
}

/** ลบทริป */
export async function deleteTrip(id: number, token: string): Promise<void> {
  const res = await fetch(`${API_BASE}/trips/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });

  // ใช้ helper เดิม เพื่อให้ 401 / 403 ได้ message เดียวกัน
  await handleAuthJson<null>(res, "ลบทริปไม่สำเร็จ");
}

/** อัปโหลดรูปทริปไปยัง Cloudinary ผ่าน backend */
export async function uploadTripPhotos(
  id: number,
  token: string,
  files: File[]
): Promise<Trip> {
  const formData = new FormData();
  files.forEach((file) => {
    // ต้องชื่อ "files" ให้ตรงกับ @RequestParam("files") ใน TripController
    formData.append("files", file);
  });

  const res = await fetch(`${API_BASE}/trips/${id}/photos`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      // ไม่ต้องใส่ Content-Type เดี๋ยว browser ใส่ boundary ให้เอง
    },
    body: formData,
  });

  return handleAuthJson<Trip>(res, "อัปโหลดรูปไม่สำเร็จ");
}

/** ลบรูปเดิมออกจากทริป */
export async function deleteTripPhoto(
  id: number,
  imageUrl: string,
  token: string
): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}/photos`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ imageUrl }),
  });

  // backend จะคืน TripResponse ตัวล่าสุดกลับมา
  return handleAuthJson<Trip>(res, "ลบรูปไม่สำเร็จ");
}