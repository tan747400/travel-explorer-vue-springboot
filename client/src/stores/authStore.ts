import { defineStore } from "pinia";

export interface AuthUser {
  userId: number;
  email: string;
  displayName: string;
  profileImageUrl: string | null;
}

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  displayName: string;
  profileImageUrl?: string | null;
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
        const parsed = JSON.parse(raw) as Partial<AuthUser>;
        return {
          userId: parsed.userId ?? 0,
          email: parsed.email ?? "",
          displayName: parsed.displayName ?? "",
          profileImageUrl: parsed.profileImageUrl ?? null,
        } as AuthUser;
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
    profileImageUrl: (state): string | null => state.user?.profileImageUrl ?? null,
  },

  actions: {
    /** ใช้ตอน login/register/upload profile/update profile สำเร็จ */
    setAuth(payload: AuthResponse) {
      this.token = payload.token;

      this.user = {
        userId: payload.userId,
        email: payload.email,
        displayName: payload.displayName,
        profileImageUrl: payload.profileImageUrl ?? null,
      };

      localStorage.setItem("token", payload.token);
      localStorage.setItem("user", JSON.stringify(this.user));
    },

    login(
      token: string,
      user: { email: string; displayName?: string; userId?: number; profileImageUrl?: string | null }
    ) {
      this.token = token;
      this.user = {
        userId: user.userId ?? this.user?.userId ?? 0,
        email: user.email,
        displayName: user.displayName || "",
        profileImageUrl: user.profileImageUrl ?? this.user?.profileImageUrl ?? null,
      };

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(this.user));
    },

    setProfileImageUrl(url: string | null) {
      if (!this.user) return;
      this.user.profileImageUrl = url;
      localStorage.setItem("user", JSON.stringify(this.user));
    }

    ,
    setDisplayName(name: string) {
      if (!this.user) return;
      this.user.displayName = name;
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