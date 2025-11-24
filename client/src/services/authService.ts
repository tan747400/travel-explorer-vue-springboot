import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// ==========================
// Auth Types
// ==========================
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

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
}

// ==========================
// LOGIN
// ==========================
export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await axios.post<AuthResponse>(
    `${API_BASE_URL}/api/auth/login`,
    payload
  );
  return data;
}

// ==========================
// REGISTER
// ==========================
export async function register(
  payload: RegisterPayload
): Promise<AuthResponse> {
  const { data } = await axios.post<AuthResponse>(
    `${API_BASE_URL}/api/auth/register`,
    payload
  );
  return data;
}

// ==========================
// CHANGE PASSWORD
// ==========================
export async function changePassword(
  token: string,
  payload: ChangePasswordPayload
): Promise<void> {
  const res = await axios.post(
    `${API_BASE_URL}/api/auth/change-password`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  // ถ้าสำเร็จ backend จะส่ง 200/204
  if (res.status !== 200 && res.status !== 204) {
    throw new Error("เปลี่ยนรหัสผ่านไม่สำเร็จ");
  }
}