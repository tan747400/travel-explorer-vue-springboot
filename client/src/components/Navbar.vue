<template>
  <header class="border-b bg-white sticky top-0 z-40">
    <div
      class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4"
    >
      <!-- Logo -->
      <button
        type="button"
        class="font-bold text-lg sm:text-xl text-sky-700 flex items-center gap-2"
        @click="goHome"
      >
        <span
          class="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-sky-100 text-sky-600 text-base font-semibold"
        >
          TE
        </span>
        <span class="hidden sm:inline-block">Travel Explorer</span>
        <span class="sm:hidden">Travel</span>
      </button>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-4 text-sm">
        <template v-if="isLoggedIn">
          <!-- Avatar + dropdown (desktop) -->
          <div class="relative">
            <button
              type="button"
              class="flex items-center gap-3 px-3 py-2 rounded-full hover:bg-slate-50 border border-transparent hover:border-slate-200 transition-colors"
              @click="toggleProfileMenu"
            >
              <div
                class="h-9 w-9 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-sm font-semibold shadow-sm overflow-hidden"
              >
                <img
                  v-if="avatarUrl"
                  :src="avatarUrl"
                  alt="User avatar"
                  class="h-full w-full object-cover"
                />
                <span v-else>{{ initials }}</span>
              </div>

              <div class="flex flex-col justify-center leading-tight">
                <span class="text-sm font-semibold text-slate-900">
                  {{ displayNameOrEmail }}
                </span>
              </div>

              <span class="text-slate-400 text-xs">▾</span>
            </button>

            <!-- desktop dropdown -->
            <transition name="fade">
              <div
                v-if="showProfileMenu"
                class="absolute right-0 mt-2 w-64 rounded-2xl border border-slate-200 bg-white shadow-lg py-2"
              >
                <div class="px-4 pb-3 border-b border-slate-100">
                  <p class="text-sm font-semibold text-slate-900 truncate">
                    {{ displayNameOrEmail }}
                  </p>
                  <p
                    v-if="auth.userEmail"
                    class="text-xs text-slate-500 truncate"
                  >
                    {{ auth.userEmail }}
                  </p>
                </div>

                <button
                  type="button"
                  class="w-full text-left px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                  @click="goProfileFromMenu"
                >
                  ดูโปรไฟล์
                </button>

                <button
                  type="button"
                  class="w-full text-left px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50"
                  @click="goDashboardFromMenu"
                >
                  ไปที่ Dashboard
                </button>

                <div class="border-t border-slate-100 mt-1 pt-1">
                  <button
                    type="button"
                    class="w-full text-left px-4 py-2.5 text-sm text-red-600 hover:bg-red-50"
                    @click="handleLogoutFromMenu"
                  >
                    ออกจากระบบ
                  </button>
                </div>
              </div>
            </transition>
          </div>
        </template>

        <!-- Guest -->
        <template v-else>
          <button
            type="button"
            class="px-3 py-1.5 rounded-full border border-sky-500 text-sky-600 hover:bg-sky-50"
            @click="goLogin"
          >
            Login
          </button>
          <button
            type="button"
            class="px-3 py-1.5 rounded-full bg-sky-600 text-white hover:bg-sky-700"
            @click="goRegister"
          >
            Register
          </button>
        </template>
      </nav>

      <!-- Mobile: hamburger only (no avatar) -->
      <div class="flex items-center md:hidden">
        <button
          type="button"
          @click="toggleMobile"
          class="h-9 w-9 inline-flex items-center justify-center rounded-full border border-slate-300 bg-white hover:bg-slate-50"
        >
          <div class="space-y-1">
            <span
              class="block h-0.5 w-4 bg-slate-700 transition-transform"
              :class="isMobileOpen ? 'translate-y-[5px] rotate-45' : ''"
            />
            <span
              class="block h-0.5 w-4 bg-slate-700 transition-opacity"
              :class="isMobileOpen ? 'opacity-0' : 'opacity-100'"
            />
            <span
              class="block h-0.5 w-4 bg-slate-700 transition-transform"
              :class="isMobileOpen ? '-translate-y-[5px] -rotate-45' : ''"
            />
          </div>
        </button>
      </div>
    </div>

    <!-- MOBILE DROPDOWN (small card under hamburger) -->
    <transition name="fade">
      <div
        v-if="isMobileOpen"
        class="fixed inset-0 z-50 md:hidden bg-transparent"
        @click.self="closeMobile"
      >
        <!-- card -->
        <div
          class="absolute right-3 top-14 w-72 rounded-2xl border border-slate-200 bg-white shadow-xl overflow-hidden"
        >
          <!-- header row -->
          <div
            class="flex items-center justify-between px-4 py-2 border-b border-slate-100"
          >
            <span class="text-sm font-semibold text-slate-900">Menu</span>
            <button
              class="h-7 w-7 flex items-center justify-center rounded-full hover:bg-slate-100 text-slate-500 text-sm"
              @click.stop="closeMobile"
            >
              ✕
            </button>
          </div>

          <div class="px-4 py-3 space-y-3">
            <!-- Logged in -->
            <template v-if="isLoggedIn">
              <div class="flex items-center gap-3">
                <div
                  class="h-9 w-9 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-xs font-semibold shadow-sm overflow-hidden"
                >
                  <img
                    v-if="avatarUrl"
                    :src="avatarUrl"
                    alt="User avatar"
                    class="h-full w-full object-cover"
                  />
                  <span v-else>{{ initials }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-sm font-semibold text-slate-900 truncate">
                    {{ displayNameOrEmail }}
                  </span>
                  <span
                    v-if="auth.userEmail"
                    class="text-xs text-slate-500 truncate"
                  >
                    {{ auth.userEmail }}
                  </span>
                </div>
              </div>

              <div class="pt-1 space-y-1.5">
                <button
                  type="button"
                  class="w-full text-left px-3 py-2 rounded-lg text-sm text-slate-700 hover:bg-slate-50"
                  @click="goProfileMobile"
                >
                  ดูโปรไฟล์
                </button>
                <button
                  type="button"
                  class="w-full text-left px-3 py-2 rounded-lg text-sm text-slate-700 hover:bg-slate-50"
                  @click="goDashboardMobile"
                >
                  ไปที่ Dashboard
                </button>
                <button
                  type="button"
                  class="w-full text-left px-3 py-2 rounded-lg text-sm text-red-600 hover:bg-red-50"
                  @click="handleLogoutMobile"
                >
                  ออกจากระบบ
                </button>
              </div>
            </template>

            <!-- Guest -->
            <template v-else>
              <div class="space-y-2">
                <button
                  type="button"
                  class="w-full px-3 py-2 rounded-full border border-sky-500 text-sky-600 text-sm hover:bg-sky-50"
                  @click="goLoginMobile"
                >
                  Login
                </button>
                <button
                  type="button"
                  class="w-full px-3 py-2 rounded-full bg-sky-600 text-white text-sm hover:bg-sky-700"
                  @click="goRegisterMobile"
                >
                  Register
                </button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </transition>
  </header>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const auth = useAuthStore();

const isLoggedIn = computed(() => auth.isLoggedIn);
const avatarUrl = computed(() => auth.profileImageUrl || "");

const initials = computed(() => {
  const base = auth.displayName || auth.userEmail || "";
  if (!base.trim()) return "?";
  const parts = base.trim().split(" ");
  return parts[0].charAt(0).toUpperCase();
});

const displayNameOrEmail = computed(
  () => auth.displayName || auth.userEmail || "ผู้ใช้งาน"
);

const showProfileMenu = ref(false);
const isMobileOpen = ref(false);

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
function goProfile() {
  router.push({ name: "profile" });
}

function toggleProfileMenu() {
  showProfileMenu.value = !showProfileMenu.value;
}

function goProfileFromMenu() {
  showProfileMenu.value = false;
  goProfile();
}
function goDashboardFromMenu() {
  showProfileMenu.value = false;
  goDashboard();
}
function handleLogoutFromMenu() {
  showProfileMenu.value = false;
  handleLogout();
}

function toggleMobile() {
  isMobileOpen.value = !isMobileOpen.value;
}
function closeMobile() {
  isMobileOpen.value = false;
}

function goLoginMobile() {
  closeMobile();
  goLogin();
}
function goRegisterMobile() {
  closeMobile();
  goRegister();
}
function goDashboardMobile() {
  closeMobile();
  goDashboard();
}
function goProfileMobile() {
  closeMobile();
  goProfile();
}
function handleLogoutMobile() {
  closeMobile();
  handleLogout();
}

function handleLogout() {
  auth.logout();
  router.push({ name: "home" });
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>