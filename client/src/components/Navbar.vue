<template>
  <header class="border-b bg-white sticky top-0 z-40">
    <div
      class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4"
    >
      <!-- โลโก้ -->
      <button
        class="font-bold text-lg sm:text-xl text-sky-700 flex items-center gap-2"
        type="button"
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
        <!-- Logged-in -->
        <template v-if="isLoggedIn">
          <!-- Avatar + dropdown -->
          <div class="relative">
            <button
              type="button"
              class="flex items-center gap-3 px-3 py-2 rounded-full hover:bg-slate-50 border border-transparent hover:border-slate-200 transition-colors"
              @click="toggleProfileMenu"
            >
              <!-- avatar -->
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

              <!-- ชื่อ -->
              <div class="flex flex-col justify-center leading-tight">
                <span class="text-sm font-semibold text-slate-900">
                  {{ displayNameOrEmail }}
                </span>
              </div>

              <!-- caret -->
              <span class="text-slate-400 text-xs" aria-hidden="true">
                ▾
              </span>
            </button>

            <!-- dropdown -->
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
            class="px-3 py-1.5 rounded-full border border-sky-500 text-sky-600 hover:bg-sky-50 transition-colors"
            @click="goLogin"
          >
            Login
          </button>

          <button
            type="button"
            class="px-3 py-1.5 rounded-full bg-sky-600 text-white hover:bg-sky-700 transition-colors"
            @click="goRegister"
          >
            Register
          </button>
        </template>
      </nav>

      <!-- Mobile: avatar + hamburger -->
      <div class="flex items-center gap-2 md:hidden">
        <button
          v-if="isLoggedIn"
          type="button"
          class="h-8 w-8 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-xs font-semibold shadow-sm overflow-hidden"
          @click="goProfile"
        >
          <img
            v-if="avatarUrl"
            :src="avatarUrl"
            alt="User avatar"
            class="h-full w-full object-cover"
          />
          <span v-else>{{ initials }}</span>
        </button>

        <button
          type="button"
          class="h-9 w-9 inline-flex items-center justify-center rounded-lg border border-slate-200 bg-white hover:bg-slate-50"
          @click="toggleMobile"
        >
          <div class="space-y-1.5">
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
  </header>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const auth = useAuthStore();

const isLoggedIn = computed(() => auth.isLoggedIn);

const initials = computed(() => {
  const base = auth.displayName || auth.userEmail || "";
  if (!base.trim()) return "?";
  const parts = base.trim().split(" ");
  const first = parts[0] ?? "";
  if (!first) return "?";
  return parts.length === 1
    ? first.charAt(0).toUpperCase()
    : (first.charAt(0) + (parts[1]?.charAt(0) ?? "")).toUpperCase();
});

const avatarUrl = computed(() => auth.profileImageUrl || "");

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