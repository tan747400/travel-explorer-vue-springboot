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
     * จะให้ทำงานเหมือน setAuth แต่ไม่บังคับต้องมี userId
     */
    login(token: string, user: { email: string; displayName?: string }) {
      this.token = token;
      this.user = {
        userId: this.user?.userId ?? 0, // ถ้าไม่มีให้เป็น 0 ไปก่อน
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