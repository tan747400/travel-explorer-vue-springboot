import { defineStore } from "pinia";

interface User {
  email: string;
  displayName?: string;
}

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    user: JSON.parse(localStorage.getItem("user") || "null") as User | null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
  },

  actions: {
    // เรียกตอน login สำเร็จ
    login(token: string, user: User) {
      this.token = token;
      this.user = user;

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(user));
    },

    // logout
    logout() {
      this.token = "";
      this.user = null;

      localStorage.removeItem("token");
      localStorage.removeItem("user");
    },
  },
});