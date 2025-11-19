<template>
  <div class="min-h-screen bg-slate-50">
    <!-- ==== Navbar อยู่ทุกหน้า ==== -->
    <header class="border-b bg-white">
      <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        <!-- โลโก้ / ชื่อเว็บ กดแล้วกลับหน้าแรก -->
        <RouterLink
          to="/"
          class="font-bold text-xl text-sky-700"
        >
          Travel Explorer
        </RouterLink>

        <!-- ขวา: ปุ่ม Login / Logout -->
        <nav class="flex items-center gap-3">
          <!-- ถ้ายังไม่ล็อกอิน -->
          <template v-if="!isLoggedIn">
            <RouterLink
              :to="{ name: 'login' }"
              class="text-sm px-3 py-1.5 rounded-md border border-sky-500 text-sky-600 hover:bg-sky-50"
            >
              Login
            </RouterLink>

            <!-- Register ไว้ก่อน เผื่อทำทีหลัง -->
            <button
              type="button"
              class="text-sm px-3 py-1.5 rounded-md bg-sky-600 text-white hover:bg-sky-700"
            >
              Register
            </button>
          </template>

          <!-- ถ้าล็อกอินแล้ว -->
          <template v-else>
            <span class="text-sm text-gray-700">
              {{ user?.displayName || user?.email }}
            </span>

            <button
              type="button"
              class="text-sm px-3 py-1.5 rounded-md border border-slate-300 hover:bg-slate-50"
              @click="handleLogout"
            >
              Logout
            </button>
          </template>
        </nav>
      </div>
    </header>

    <!-- ==== เนื้อหาแต่ละหน้า ==== -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { RouterLink, useRouter } from "vue-router";
import { useAuth } from "@/composables/useAuth"; // 👈 ใช้ composable ที่เราสร้าง

const router = useRouter();
const { isLoggedIn, user, logout } = useAuth();

function handleLogout() {
  logout();
  router.push("/"); // กลับหน้า Home หลัง logout
}
</script>