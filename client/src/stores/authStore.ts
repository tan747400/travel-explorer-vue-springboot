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

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    user: ((): AuthUser | null => {
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
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.user?.userId ?? null,
    userEmail: (state) => state.user?.email ?? "",
    displayName: (state) => state.user?.displayName ?? "",
  },

  actions: {
    /**
     * ใช้ตอน login สำเร็จ รับทั้ง AuthResponse จาก backend
     */
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

    /**
     * เผื่อโค้ดเดิมที่ยังเรียก authStore.login(token, user)
     * จะ map ให้เข้าโครงสร้างใหม่ (ถ้าไม่มี userId ให้ใช้ 0 ไปก่อน)
     */
    login(token: string, user: { email: string; displayName?: string; userId?: number }) {
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