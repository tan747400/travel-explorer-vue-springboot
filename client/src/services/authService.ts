import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  displayName: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
  displayName: string;
}

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await axios.post<AuthResponse>(
    `${API_BASE_URL}/api/auth/login`,
    payload
  );
  return data;
}

export async function register(payload: RegisterPayload): Promise<AuthResponse> {
  const { data } = await axios.post<AuthResponse>(
    `${API_BASE_URL}/api/auth/register`,
    payload
  );
  return data;
}