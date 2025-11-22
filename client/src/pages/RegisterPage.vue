<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50">
    <div class="w-full max-w-md bg-white rounded-2xl shadow p-6 space-y-4">
      <h1 class="text-2xl font-bold text-center">สมัครสมาชิก</h1>

      <form @submit.prevent="handleRegister" class="space-y-4">
        <!-- Display Name -->
        <div>
          <label class="block text-sm mb-1">ชื่อที่แสดง</label>
          <input
            v-model="displayName"
            type="text"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            required
          />
        </div>

        <!-- Email -->
        <div>
          <label class="block text-sm mb-1">อีเมล</label>
          <input
            v-model="email"
            type="email"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            required
          />
        </div>

        <!-- Password -->
        <div>
          <label class="block text-sm mb-1">รหัสผ่าน</label>
          <input
            v-model="password"
            type="password"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            required
          />
        </div>

        <!-- Error -->
        <p v-if="error" class="text-sm text-red-500">
          {{ error }}
        </p>

        <!-- Submit -->
        <button
          type="submit"
          class="w-full bg-sky-600 hover:bg-sky-700 text-white py-2 rounded-lg text-sm font-medium disabled:opacity-60 disabled:cursor-not-allowed"
          :disabled="loading"
        >
          {{ loading ? "กำลังสมัคร..." : "สมัครสมาชิก" }}
        </button>
      </form>

      <p class="text-sm text-center mt-4">
        มีบัญชีอยู่แล้ว?
        <router-link class="text-sky-600 hover:underline" to="/login">
          เข้าสู่ระบบ
        </router-link>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import {
  register,
  type AuthResponse,
} from "@/services/authService";

const router = useRouter();
const toast = useToast();
const auth = useAuthStore();

const displayName = ref("");
const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

async function handleRegister() {
  error.value = "";
  loading.value = true;

  try {
    const payload = {
      email: email.value.trim(),
      password: password.value.trim(),
      displayName: displayName.value.trim(),
    };

    const res = (await register(payload)) as AuthResponse;

    //  Auto login หลังสมัครเสร็จ
    auth.login(res.token, {
      email: res.email,
      displayName: res.displayName,
    });

    toast.success("สมัครสมาชิกและเข้าสู่ระบบเรียบร้อย 🎉");

    // Redirect ไปหน้า Home
    await router.push({ name: "home" });

  } catch (err: any) {
    console.error(err);

    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "สมัครสมาชิกไม่สำเร็จ กรุณาลองใหม่อีกครั้ง";

    error.value = msg;
    toast.error(msg);
  } finally {
    loading.value = false;
  }
}
</script>