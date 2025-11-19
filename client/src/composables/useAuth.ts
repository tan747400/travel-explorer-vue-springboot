import { ref, computed } from "vue";
import type { AuthResponse } from "@/services/authService";

const token = ref<string | null>(localStorage.getItem("auth_token"));
const user = ref<Pick<AuthResponse, "userId" | "email" | "displayName"> | null>(
  localStorage.getItem("auth_user")
    ? JSON.parse(localStorage.getItem("auth_user") as string)
    : null
);

export function useAuth() {
  const isLoggedIn = computed(() => !!token.value);

  function setAuth(data: AuthResponse) {
    token.value = data.token;
    user.value = {
      userId: data.userId,
      email: data.email,
      displayName: data.displayName,
    };

    localStorage.setItem("auth_token", data.token);
    localStorage.setItem("auth_user", JSON.stringify(user.value));
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem("auth_token");
    localStorage.removeItem("auth_user");
  }

  return {
    token,
    user,
    isLoggedIn,
    setAuth,
    logout,
  };
}