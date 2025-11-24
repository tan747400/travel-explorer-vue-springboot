<template>
    <div class="min-h-screen bg-slate-50">
      <main class="max-w-xl mx-auto px-4 py-10">
        <!-- Header -->
        <header class="mb-6 flex items-center justify-between">
          <div>
            <h1 class="text-2xl md:text-3xl font-bold">โปรไฟล์ของฉัน</h1>
            <p class="text-xs text-slate-500 mt-1">
              ดูข้อมูลผู้ใช้, token expiry และเปลี่ยนรหัสผ่าน
            </p>
          </div>
  
          <button
            type="button"
            class="text-xs text-sky-600 hover:underline"
            @click="goHome"
          >
            ← กลับหน้าแรก
          </button>
        </header>
  
        <!-- ข้อมูลผู้ใช้ -->
        <section
          class="mb-6 rounded-2xl bg-white border border-slate-200 shadow-sm px-4 py-4 space-y-3"
        >
          <h2 class="text-sm font-semibold text-slate-800">
            ข้อมูลผู้ใช้
          </h2>
  
          <div class="space-y-2 text-sm">
            <div class="flex items-center justify-between gap-3">
              <span class="text-slate-500 w-24">ชื่อ</span>
              <span class="flex-1 text-right font-medium text-slate-800 truncate">
                {{ displayName || "-" }}
              </span>
            </div>
  
            <div class="flex items-center justify-between gap-3">
              <span class="text-slate-500 w-24">อีเมล</span>
              <span class="flex-1 text-right text-slate-800 truncate">
                {{ email || "-" }}
              </span>
            </div>
  
            <div class="flex items-center justify-between gap-3">
              <span class="text-slate-500 w-24">Token หมดอายุ</span>
              <span class="flex-1 text-right text-xs text-slate-700">
                <span v-if="expiresAt">
                  {{ expiresAtText }}
                  <span class="ml-1 text-[11px]" v-if="expiresInText">
                    (เหลือ {{ expiresInText }})
                  </span>
                </span>
                <span v-else>-</span>
              </span>
            </div>
          </div>
  
          <div class="pt-3 border-t border-slate-100 flex justify-end">
            <button
              type="button"
              class="px-3 py-1.5 rounded-md border text-xs text-slate-600 hover:bg-slate-50"
              @click="handleLogout"
            >
              Logout
            </button>
          </div>
        </section>
  
        <!-- เปลี่ยนรหัสผ่าน -->
        <section
          class="rounded-2xl bg-white border border-slate-200 shadow-sm px-4 py-4"
        >
          <h2 class="text-sm font-semibold text-slate-800 mb-2">
            เปลี่ยนรหัสผ่าน
          </h2>
  
          <form class="space-y-3" @submit.prevent="handleChangePassword">
            <div>
              <label class="block text-xs font-medium text-slate-600 mb-1">
                รหัสผ่านปัจจุบัน
              </label>
              <input
                v-model="currentPassword"
                type="password"
                class="w-full border rounded-md px-3 py-2 text-sm
                       focus:outline-none focus:ring-1 focus:ring-sky-500"
                autocomplete="current-password"
                required
              />
            </div>
  
            <div>
              <label class="block text-xs font-medium text-slate-600 mb-1">
                รหัสผ่านใหม่
              </label>
              <input
                v-model="newPassword"
                type="password"
                class="w-full border rounded-md px-3 py-2 text-sm
                       focus:outline-none focus:ring-1 focus:ring-sky-500"
                autocomplete="new-password"
                required
              />
            </div>
  
            <div>
              <label class="block text-xs font-medium text-slate-600 mb-1">
                ยืนยันรหัสผ่านใหม่
              </label>
              <input
                v-model="confirmPassword"
                type="password"
                class="w-full border rounded-md px-3 py-2 text-sm
                       focus:outline-none focus:ring-1 focus:ring-sky-500"
                autocomplete="new-password"
                required
              />
            </div>
  
            <!-- error / success -->
            <p
              v-if="changeError"
              class="text-xs text-red-600 bg-red-50 border border-red-100 rounded-md px-3 py-2"
            >
              {{ changeError }}
            </p>
            <p
              v-if="changeSuccess"
              class="text-xs text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-md px-3 py-2"
            >
              {{ changeSuccess }}
            </p>
  
            <div class="pt-1 flex justify-end">
              <button
                type="submit"
                class="px-4 py-2 rounded-md bg-sky-600 text-white text-sm
                       hover:bg-sky-700 disabled:opacity-60 disabled:cursor-not-allowed"
                :disabled="changing"
              >
                {{ changing ? "กำลังเปลี่ยนรหัสผ่าน..." : "บันทึกรหัสผ่านใหม่" }}
              </button>
            </div>
          </form>
        </section>
      </main>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, ref } from "vue";
  import { useRouter } from "vue-router";
  import { useAuthStore } from "@/stores/authStore";
  import { changePassword } from "@/services/authService"; // เดี๋ยวให้ตัวอย่างฟังก์ชันด้านล่าง
  import { useToast } from "vue-toastification";
  
  const router = useRouter();
  const auth = useAuthStore();
  const toast = useToast();
  
  /* ========= ข้อมูลจาก store / token ========= */
  
  const token = computed(() => auth.token || "");
  
  // สมมติว่า authStore มี user ที่เก็บชื่อ/อีเมลไว้ ถ้า key ไม่ตรงปรับชื่อ field ได้เลย
  const displayName = computed(
    () =>
      (auth.user as any)?.displayName ||
      (auth.user as any)?.name ||
      (auth.user as any)?.username ||
      ""
  );
  
  const email = computed(
    () => (auth.user as any)?.email || decodeTokenPayload()?.email || ""
  );
  
  // decode JWT แบบเบา ๆ ไม่ต้องลง library เพิ่ม
  function decodeTokenPayload():
    | { exp?: number; email?: string; sub?: string; [key: string]: any }
    | null {
    if (!token.value) return null;
    try {
      const [, payload] = token.value.split(".");
      if (!payload) return null;
      const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
      const json = atob(base64);
      return JSON.parse(json);
    } catch (e) {
      console.warn("Cannot decode JWT payload", e);
      return null;
    }
  }
  
  const expiresAt = computed<Date | null>(() => {
    const payload = decodeTokenPayload();
    if (!payload?.exp) return null;
    return new Date(payload.exp * 1000);
  });
  
  const expiresAtText = computed(() => {
    if (!expiresAt.value) return "";
    return expiresAt.value.toLocaleString("th-TH");
  });
  
  const expiresInText = computed(() => {
    if (!expiresAt.value) return "";
    const ms = expiresAt.value.getTime() - Date.now();
    if (ms <= 0) return "หมดอายุแล้ว";
    const minutes = Math.round(ms / 60000);
    if (minutes <= 1) return "ไม่ถึง 1 นาที";
    if (minutes < 60) return `${minutes} นาที`;
    const hours = Math.round(minutes / 60);
    return `${hours} ชม.`;
  });
  
  /* ========= เปลี่ยนรหัสผ่าน ========= */
  
  const currentPassword = ref("");
  const newPassword = ref("");
  const confirmPassword = ref("");
  
  const changing = ref(false);
  const changeError = ref("");
  const changeSuccess = ref("");
  
  async function handleChangePassword() {
    changeError.value = "";
    changeSuccess.value = "";
  
    if (!currentPassword.value || !newPassword.value) {
      changeError.value = "กรุณากรอกรหัสผ่านให้ครบ";
      return;
    }
    if (newPassword.value.length < 6) {
      changeError.value = "รหัสผ่านใหม่ต้องยาวอย่างน้อย 6 ตัวอักษร";
      return;
    }
    if (newPassword.value !== confirmPassword.value) {
      changeError.value = "รหัสผ่านใหม่และยืนยันไม่ตรงกัน";
      return;
    }
  
    if (!token.value) {
      changeError.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
      auth.logout();
      router.push({ name: "login" });
      return;
    }
  
    try {
      changing.value = true;
      await changePassword(token.value, {
        currentPassword: currentPassword.value,
        newPassword: newPassword.value,
      });
  
      changeSuccess.value = "เปลี่ยนรหัสผ่านสำเร็จ";
      toast.success("เปลี่ยนรหัสผ่านสำเร็จ");
  
      currentPassword.value = "";
      newPassword.value = "";
      confirmPassword.value = "";
    } catch (err: any) {
      console.error(err);
      changeError.value =
        err?.message || "เปลี่ยนรหัสผ่านไม่สำเร็จ กรุณาลองใหม่อีกครั้ง";
    } finally {
      changing.value = false;
    }
  }
  
  /* ========= อื่น ๆ ========= */
  
  function handleLogout() {
    auth.logout();
    router.push({ name: "login" });
  }
  
  function goHome() {
    router.push({ name: "home" });
  }
  </script>
  
  <style scoped></style>  