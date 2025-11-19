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
            class="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg text-sm font-medium"
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
  import { useRouter } from "vue-router";
  import { login } from "@/services/authService";
  import { useAuth } from "@/composables/useAuth";
  
  const router = useRouter();
  const { setAuth } = useAuth();
  
  const email = ref("");
  const password = ref("");
  const loading = ref(false);
  const error = ref("");
  
  async function handleSubmit() {
    error.value = "";
    loading.value = true;
    try {
      const res = await login({ email: email.value, password: password.value });
      setAuth(res);
      await router.push("/"); // กลับหน้า Home หรือ /dashboard ก็ได้
    } catch (err: any) {
      console.error(err);
      error.value =
        err?.response?.data?.message || "เข้าสู่ระบบไม่สำเร็จ กรุณาลองอีกครั้ง";
    } finally {
      loading.value = false;
    }
  }
  </script>  