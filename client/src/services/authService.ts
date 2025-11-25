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
  profileImageUrl?: string | null;
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

export interface UpdateProfilePayload {
  displayName: string;
}

// ==========================
// Auth APIs
// ==========================
export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const res = await axios.post(`${API_BASE_URL}/api/auth/login`, payload);
  return res.data as AuthResponse;
}

export async function register(
  payload: RegisterPayload
): Promise<AuthResponse> {
  const res = await axios.post(`${API_BASE_URL}/api/auth/register`, payload);
  return res.data as AuthResponse;
}

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

  if (res.status !== 200 && res.status !== 204) {
    throw new Error("เปลี่ยนรหัสผ่านไม่สำเร็จ");
  }
}

export async function updateProfile(
  token: string,
  payload: UpdateProfilePayload
): Promise<AuthResponse> {
  const res = await axios.put(`${API_BASE_URL}/api/auth/profile`, payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data as AuthResponse;
}

export async function uploadProfilePicture(
  token: string,
  file: File
): Promise<AuthResponse> {
  const form = new FormData();
  form.append("file", file);

  const res = await axios.post(
    `${API_BASE_URL}/api/auth/profile-picture`,
    form,
    {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return res.data as AuthResponse;
}

export async function deleteProfilePicture(token: string): Promise<void> {
  await axios.delete(`${API_BASE_URL}/api/auth/profile-picture`, {
    headers: { Authorization: `Bearer ${token}` },
  });
}