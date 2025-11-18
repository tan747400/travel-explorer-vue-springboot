import axios from "axios";
import type { Trip } from "../types/trip";

// ถ้าไม่ตั้ง ENV จะ fallback เป็น localhost:8080
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export async function getTrips(keyword = ""): Promise<Trip[]> {
  const url = `${BASE_URL}/api/trips?keywords=${encodeURIComponent(keyword)}`;

  const res = await axios.get(url);

  // รองรับทั้งกรณี { data: [...] } และ [...] ตรง ๆ
  const raw = Array.isArray(res?.data?.data)
    ? res.data.data
    : Array.isArray(res.data)
    ? res.data
    : [];

  return raw as Trip[];
}
