<template>
    <div class="min-h-screen flex items-center justify-center bg-slate-50">
      <div class="w-full max-w-md bg-white rounded-2xl shadow p-6">
        <h1 class="text-2xl font-bold mb-6 text-center">สมัครสมาชิก</h1>
  
        <form @submit.prevent="handleRegister" class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1">ชื่อที่แสดง</label>
            <input
              v-model="displayName"
              type="text"
              class="w-full border rounded-md px-3 py-2"
              required
            />
          </div>
  
          <div>
            <label class="block text-sm font-medium mb-1">อีเมล</label>
            <input
              v-model="email"
              type="email"
              class="w-full border rounded-md px-3 py-2"
              required
            />
          </div>
  
          <div>
            <label class="block text-sm font-medium mb-1">รหัสผ่าน</label>
            <input
              v-model="password"
              type="password"
              class="w-full border rounded-md px-3 py-2"
              required
            />
          </div>
  
          <button
            type="submit"
            class="w-full bg-sky-600 text-white py-2 rounded-md hover:bg-sky-700"
            :disabled="loading"
          >
            {{ loading ? "กำลังสมัคร..." : "สมัครสมาชิก" }}
          </button>
        </form>
  
        <p class="text-sm mt-4 text-center">
          มีบัญชีแล้ว?
          <router-link class="text-sky-600" to="/login">
            เข้าสู่ระบบ
          </router-link>
        </p>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from "vue";
  import { useRouter } from "vue-router";
  
  const router = useRouter();
  
  const displayName = ref("");
  const email = ref("");
  const password = ref("");
  const loading = ref(false);
  
  const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
  
  async function handleRegister() {
    loading.value = true;
  
    try {
      const res = await fetch(`${API_BASE_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: email.value,
          password: password.value,
          displayName: displayName.value,
        }),
      });
  
      if (!res.ok) {
        let msg = "สมัครสมาชิกไม่สำเร็จ";
        try {
          const data = await res.json();
          if (data?.message) msg = data.message;
        } catch (e) {
          // ignore parse error
        }
        alert(msg);
        return;
      }
  
      alert("สมัครสำเร็จ! ไปเข้าสู่ระบบได้เลย");
      router.push({ name: "login" });
    } catch (err) {
      console.error(err);
      alert("เกิดข้อผิดพลาดจากเครือข่าย");
    } finally {
      loading.value = false;
    }
  }
  </script>  