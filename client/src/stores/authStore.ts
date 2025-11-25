import { defineStore } from "pinia";

export interface AuthUser {
  userId: number;
  email: string;
  displayName: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  displayName: string;
}

interface AuthState {
  token: string;
  user: AuthUser | null;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    token: localStorage.getItem("token") || "",
    user: (() => {
      const raw = localStorage.getItem("user");
      if (!raw) return null;
      try {
        return JSON.parse(raw) as AuthUser;
      } catch {
        return null;
      }
    })(),
  }),

  getters: {
    isLoggedIn: (state): boolean => !!state.token,
    userId: (state): number | null => state.user?.userId ?? null,
    userEmail: (state): string => state.user?.email ?? "",
    displayName: (state): string => state.user?.displayName ?? "",
  },

  actions: {
    /** ใช้ตอน login/register สำเร็จ */
    setAuth(payload: AuthResponse) {
      this.token = payload.token;
      this.user = {
        userId: payload.userId,
        email: payload.email,
        displayName: payload.displayName,
      };

      localStorage.setItem("token", payload.token);
      localStorage.setItem("user", JSON.stringify(this.user));
    },

    /** เผื่อโค้ดเก่าเรียก auth.login(token, user) */
    login(
      token: string,
      user: { email: string; displayName?: string; userId?: number }
    ) {
      this.token = token;
      this.user = {
        userId: user.userId ?? this.user?.userId ?? 0,
        email: user.email,
        displayName: user.displayName || "",
      };

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(this.user));
    },

    logout() {
      this.token = "";
      this.user = null;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    },
  },
});