<template>
  <div class="min-h-screen bg-slate-50">
    <!-- ==== Navbar ==== -->
    <header class="border-b bg-white">
      <div
        class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4"
      >
        <!-- โลโก้ / ชื่อเว็บ -->
        <button
          class="font-bold text-xl text-sky-700"
          type="button"
          @click="goHome"
        >
          Travel Explorer
        </button>

        <!-- ขวา: ปุ่ม / ชื่อ user -->
        <nav class="flex items-center gap-3 text-sm">
          <!-- ถ้าล็อกอินแล้ว -->
          <template v-if="isLoggedIn">
            <span class="text-gray-700">
              {{ auth.user?.displayName || auth.user?.email }}
            </span>

            <button
              type="button"
              class="px-3 py-1.5 rounded-md border border-sky-500 text-sky-600 hover:bg-sky-50"
              @click="goDashboard"
            >
              Dashboard
            </button>

            <button
              type="button"
              class="px-3 py-1.5 rounded-md bg-sky-600 text-white hover:bg-sky-700"
              @click="handleLogout"
            >
              Logout
            </button>
          </template>

          <!-- ถ้ายังไม่ล็อกอิน -->
          <template v-else>
            <button
              type="button"
              class="px-3 py-1.5 rounded-md border border-sky-500 text-sky-600 hover:bg-sky-50"
              @click="goLogin"
            >
              Login
            </button>

            <button
              type="button"
              class="px-3 py-1.5 rounded-md bg-sky-600 text-white hover:bg-sky-700"
              @click="goRegister"
            >
              Register
            </button>
          </template>
        </nav>
      </div>
    </header>

    <!-- ==== เนื้อหาหน้าแต่ละหน้า ==== -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "./stores/authStore";

const router = useRouter();
const auth = useAuthStore();

const isLoggedIn = computed(() => !!auth.token);

function goHome() {
  router.push({ name: "home" });
}

function goLogin() {
  router.push({ name: "login" });
}

function goRegister() {
  router.push({ name: "register" });
}

function goDashboard() {
  router.push({ name: "dashboard" });
}

function handleLogout() {
  auth.logout();
  router.push({ name: "home" });
}
</script>
