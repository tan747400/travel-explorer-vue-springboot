<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50">
    <div class="w-full max-w-md bg-white rounded-2xl shadow p-6 space-y-4">
      <h1 class="text-2xl font-bold text-center">เข้าสู่ระบบ</h1>

      <form class="space-y-4" @submit.prevent="handleSubmit">
        <div>
          <label class="block text-sm mb-1">อีเมล</label>
          <input
            v-model="email"
            type="email"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            required
          />
        </div>

        <div>
          <label class="block text-sm mb-1">รหัสผ่าน</label>
          <input
            v-model="password"
            type="password"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            required
          />
        </div>

        <p v-if="error" class="text-sm text-red-500">
          {{ error }}
        </p>

        <button
          type="submit"
          class="w-full bg-sky-600 hover:bg-sky-700 text-white py-2 rounded-lg text-sm font-medium disabled:opacity-60 disabled:cursor-not-allowed"
          :disabled="loading"
        >
          {{ loading ? "กำลังเข้าสู่ระบบ..." : "เข้าสู่ระบบ" }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login } from "@/services/authService";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "vue-toastification";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const toast = useToast();

const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  displayName: string;
}

async function handleSubmit() {
  error.value = "";
  loading.value = true;

  try {
    const res = (await login({
      email: email.value,
      password: password.value,
    })) as AuthResponse;

    // อัปเดต Pinia store ให้ Navbar รู้ว่าเราล็อกอินแล้ว
    authStore.login(res.token, {
      email: res.email,
      displayName: res.displayName,
    });

    toast.success("เข้าสู่ระบบสำเร็จ 🎉");

    // ถ้ามี redirect (เช่น มาจากหน้า requiresAuth) ให้เด้งกลับไปหน้านั้น
    const redirect = (route.query.redirect as string) || null;
    if (redirect) {
      await router.push(redirect);
    } else {
      await router.push({ name: "home" });
    }
  } catch (err: any) {
    console.error(err);
    const message =
      err?.response?.data?.message ||
      "เข้าสู่ระบบไม่สำเร็จ กรุณาลองอีกครั้ง";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
}
</script>