import type { Trip, PagedTrips } from "../types/trip";

const API_BASE = "http://localhost:8080/api";

/**
 * ดึง list trips แบบมี pagination
 * @param keyword คำค้น (ชื่อ / province / ฯลฯ)
 * @param page    page index (0-based)
 * @param size    จำนวนรายการต่อหน้า
 */
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

  const data = (await res.json()) as PagedTrips;
  return data;
}

/**
 * ดึงทริปตัวเดียวตาม id
 */
export async function getTripById(id: number): Promise<Trip> {
  const res = await fetch(`${API_BASE}/trips/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch trip id=${id}: ${res.status}`);
  }
  return (await res.json()) as Trip;
}