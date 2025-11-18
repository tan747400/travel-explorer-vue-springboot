import axios from "axios";
import type { Trip } from "../types/trip";

const BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

export async function getTrips(keyword = ""): Promise<Trip[]> {
  const params = keyword ? { q: keyword } : undefined;

  const res = await axios.get<Trip[]>(`${BASE_URL}/trips`, { params });
  return res.data;
}