<template>
  <!-- ใช้ min-h-screen ให้ยืดตามความสูงจอ และให้ scroll ได้ปกติ -->
  <div class="min-h-screen bg-slate-50">
    <main
      class="w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-10 py-8 md:py-10 lg:py-14"
    >
      <!-- ถ้ายังไม่ล็อกอิน -->
      <section
        v-if="!auth.isLoggedIn"
        class="rounded-2xl bg-white border border-slate-200 px-6 py-8 text-center shadow-sm"
      >
        <h1 class="text-2xl font-bold text-slate-900 mb-2">
          โปรไฟล์ผู้ใช้
        </h1>
        <p class="text-sm text-slate-600 mb-4">
          คุณยังไม่ได้เข้าสู่ระบบ
        </p>
        <RouterLink
          :to="{ name: 'login' }"
          class="inline-flex items-center px-4 py-2 rounded-full bg-sky-600 text-white text-sm font-medium hover:bg-sky-700"
        >
          ไปที่หน้าเข้าสู่ระบบ
        </RouterLink>
      </section>

      <!-- ถ้าล็อกอินแล้ว -->
      <section
        v-else
        class="rounded-3xl bg-white border border-slate-200 shadow-sm px-4 sm:px-6 md:px-8 py-6 sm:py-8 md:py-10"
      >
        <!-- Header: avatar + ชื่อ -->
        <header
          class="flex flex-col sm:flex-row sm:items-center gap-4 sm:gap-5 border-b border-slate-100 pb-5 mb-6"
        >
          <div
            class="h-16 w-16 sm:h-18 sm:w-18 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-2xl font-semibold shadow-md overflow-hidden mx-auto sm:mx-0"
          >
            <img
              v-if="avatarUrl"
              :src="avatarUrl"
              alt="Profile avatar"
              class="h-full w-full object-cover"
            />
            <span v-else>
              {{ initials }}
            </span>
          </div>

          <div class="text-center sm:text-left">
            <h1 class="text-2xl sm:text-3xl font-bold text-slate-900">
              {{ auth.displayName || "ผู้ใช้งาน" }}
            </h1>
            <p class="text-sm text-slate-500 mt-0.5">
              {{ auth.userEmail }}
            </p>
          </div>
        </header>

        <div class="flex flex-col md:flex-row gap-6 md:gap-8">
          <!-- ซ้าย: เมนูแท็บ -->
          <aside class="md:w-52 flex-shrink-0">
            <nav
              class="rounded-2xl bg-slate-50 border border-slate-200 shadow-sm py-3"
            >
              <button
                type="button"
                class="w-full flex items-center gap-3 px-4 py-2.5 text-sm"
                :class="activeTab === 'profile'
                  ? 'text-slate-900 font-semibold bg-white border-r-4 border-sky-500'
                  : 'text-slate-600 hover:bg-white'"
                @click="activeTab = 'profile'"
              >
                <span class="text-lg">👤</span>
                <span>Profile</span>
              </button>

              <button
                type="button"
                class="w-full flex items-center gap-3 px-4 py-2.5 text-sm"
                :class="activeTab === 'password'
                  ? 'text-slate-900 font-semibold bg-white border-r-4 border-sky-500'
                  : 'text-slate-600 hover:bg-white'"
                @click="activeTab = 'password'"
              >
                <span class="text-lg">🔒</span>
                <span>Reset password</span>
              </button>
            </nav>
          </aside>

          <!-- ขวา: เนื้อหาแต่ละแท็บ -->
          <section
            class="flex-1 rounded-2xl bg-slate-50 border border-slate-200 shadow-sm px-5 sm:px-6 py-5 sm:py-6"
          >
            <!-- Tab: Profile -->
            <div v-if="activeTab === 'profile'" class="space-y-6">
              <!-- Avatar + upload -->
              <div
                class="flex flex-col sm:flex-row sm:items-center gap-6 border-b border-slate-100 pb-5"
              >
                <div class="relative mx-auto sm:mx-0">
                  <div
                    class="h-28 w-28 sm:h-32 sm:w-32 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-3xl font-semibold shadow-md overflow-hidden"
                  >
                    <img
                      v-if="avatarUrl"
                      :src="avatarUrl"
                      alt="Profile avatar"
                      class="h-full w-full object-cover"
                    />
                    <span v-else>
                      {{ initials }}
                    </span>
                  </div>

                  <!-- ปุ่มลบรูป -->
                  <button
                    v-if="avatarUrl"
                    type="button"
                    class="absolute -top-1.5 -right-1.5 h-7 w-7 rounded-full bg-white border border-slate-200 shadow flex items-center justify-center text-slate-500 hover:bg-slate-50 text-xs"
                    @click="handleRemoveAvatar"
                    aria-label="ลบรูปโปรไฟล์"
                  >
                    ✕
                  </button>
                </div>

                <div
                  class="flex flex-col gap-2 flex-1 text-center sm:text-left"
                >
                  <button
                    type="button"
                    class="inline-flex items-center justify-center w-full sm:w-auto px-4 py-2 rounded-full border border-slate-300 bg-white text-sm text-slate-700 hover:bg-slate-50"
                    @click="triggerUpload"
                  >
                    เลือกรูปโปรไฟล์จากเครื่อง
                  </button>
                  <p class="text-xs text-slate-400">
                    เลือกรูปแล้วจะเห็นตัวอย่างทันที
                    แต่จะอัปโหลดจริงเมื่อกดปุ่ม
                    <span class="font-semibold">Save</span>
                  </p>

                  <input
                    ref="fileInput"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="onAvatarSelected"
                  />
                </div>
              </div>

              <!-- ฟอร์มข้อมูลพื้นฐาน -->
              <form class="space-y-4 pt-1" @submit.prevent="handleSaveProfile">
                <div class="space-y-1">
                  <label class="block text-xs font-medium text-slate-700">
                    Name
                  </label>
                  <input
                    v-model="profileName"
                    type="text"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    placeholder="Full name"
                  />
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-medium text-slate-700">
                    Username
                  </label>
                  <input
                    v-model="profileUsername"
                    type="text"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    placeholder="Username"
                    disabled
                  />
                </div>

                <div class="space-y-1">
                  <label class="block text-xs font-medium text-slate-700">
                    Email
                  </label>
                  <input
                    :value="auth.userEmail"
                    type="email"
                    class="w-full border border-slate-200 bg-slate-100 rounded-lg px-3 py-2 text-sm text-slate-500 cursor-not-allowed"
                    disabled
                  />
                </div>

                <div class="pt-2">
                  <button
                    type="submit"
                    class="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 disabled:opacity-60 disabled:cursor-not-allowed"
                    :disabled="savingProfile"
                  >
                    <span v-if="savingProfile">กำลังบันทึก...</span>
                    <span v-else>Save</span>
                  </button>
                </div>
              </form>
            </div>

            <!-- Tab: Reset password -->
            <div v-else class="space-y-6">
              <div class="space-y-1">
                <h2 class="text-lg font-semibold text-slate-900">
                  Reset password
                </h2>
                <p class="text-xs sm:text-sm text-slate-500">
                  เพื่อความปลอดภัย แนะนำให้เปลี่ยนรหัสผ่านเป็นระยะ ๆ
                  และใช้รหัสผ่านที่คาดเดายาก
                </p>
              </div>

              <form
                class="space-y-3"
                @submit.prevent="handleChangePassword"
              >
                <div>
                  <label
                    class="block text-xs font-medium text-slate-700 mb-1"
                  >
                    Current password
                  </label>
                  <input
                    v-model="currentPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="current-password"
                    required
                    @input="passwordError = ''"
                  />
                </div>

                <div>
                  <label
                    class="block text-xs font-medium text-slate-700 mb-1"
                  >
                    New password
                  </label>
                  <input
                    v-model="newPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="new-password"
                    required
                    @input="passwordError = ''"
                  />
                </div>

                <div>
                  <label
                    class="block text-xs font-medium text-slate-700 mb-1"
                  >
                    Confirm new password
                  </label>
                  <input
                    v-model="confirmPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="new-password"
                    required
                    @input="passwordError = ''"
                  />
                </div>

                <p v-if="passwordError" class="text-xs text-red-500">
                  {{ passwordError }}
                </p>

                <div class="pt-2">
                  <button
                    type="submit"
                    class="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 disabled:opacity-60 disabled:cursor-not-allowed"
                    :disabled="changingPassword"
                  >
                    <span v-if="changingPassword">
                      กำลังเปลี่ยนรหัสผ่าน...
                    </span>
                    <span v-else>Reset password</span>
                  </button>
                </div>
              </form>
            </div>
          </section>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "vue-toastification";
import {
  changePassword,
  uploadProfilePicture,
  deleteProfilePicture,
  updateProfile,
} from "@/services/authService";
import { useSessionExpired } from "@/composables/useSessionExpired";

const auth = useAuthStore();
const toast = useToast();
const { handleSessionExpired } = useSessionExpired();

/* ========== UI state ========== */
const activeTab = ref<"profile" | "password">("profile");

// avatar url ปัจจุบันที่แสดงในหน้า
const avatarUrl = ref<string | null>(auth.profileImageUrl);
// เก็บค่า avatar เดิมจาก backend ไว้ใช้ตัดสินใจลบ
const originalAvatarUrl = ref<string | null>(auth.profileImageUrl);
// ไฟล์ที่ผู้ใช้เลือกไว้แต่ยังไม่อัปโหลด
const selectedAvatarFile = ref<File | null>(null);
// URL ที่สร้างจาก URL.createObjectURL เพื่อ preview
const avatarPreviewUrl = ref<string | null>(null);
// flag ว่าผู้ใช้กดลบรูป (แล้วจะไปลบจริงตอน Save)
const avatarDeleteRequested = ref(false);

const fileInput = ref<HTMLInputElement | null>(null);

// ชื่อที่อยู่ในฟอร์ม (กำลังแก้ไข) แยกจากชื่อที่แสดงด้านบน
const profileName = ref(auth.displayName || "");
const profileUsername = ref(""); // ยังไม่ได้ใช้ backend เลยปิดแก้ไขไว้

// ฟอร์มเปลี่ยนรหัสผ่าน
const currentPassword = ref("");
const newPassword = ref("");
const confirmPassword = ref("");
const passwordError = ref("");
const changingPassword = ref(false);

// saving profile
const savingProfile = ref(false);

/* ========== Helpers ========== */

// Initials สำหรับ avatar – ใช้ชื่อที่บันทึกแล้วจาก auth เป็นหลัก
const initials = computed(() => {
  const base = auth.displayName || auth.userEmail || "";
  if (!base.trim()) return "?";

  const parts = base.trim().split(" ");
  const first = parts[0];
  if (!first) return "?";

  if (parts.length === 1) {
    return first.charAt(0).toUpperCase();
  }

  const second = parts[1] || "";
  return (first.charAt(0) + (second.charAt(0) || "")).toUpperCase();
});

/**
 * เช็คว่า token ผิดรูปแบบ / หมดอายุไหม
 */
function isTokenInvalidOrExpired(token: string | null | undefined): boolean {
  if (!token) return true;

  try {
    const parts = token.split(".");
    if (parts.length !== 3) return true;

    const [, payloadPart] = parts;
    if (!payloadPart) return true;

    const payloadBase64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const json = atob(payloadBase64);
    const payload = JSON.parse(json) as { exp?: number };

    if (!payload.exp) {
      // ไม่มี exp → ให้ backend เป็นคนบอก 401 แทน
      return false;
    }

    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
  } catch {
    return true;
  }
}

/* ตอนเข้าเพจ: ถ้า token ผิด/หมดอายุ ให้เด้งออกเลย */
onMounted(() => {
  if (isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
  }
});

/* ========== Avatar: เลือกรูป + preview (ยังไม่อัปโหลด) ========== */
function triggerUpload() {
  fileInput.value?.click();
}

function onAvatarSelected(e: Event) {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  if (avatarPreviewUrl.value) {
    URL.revokeObjectURL(avatarPreviewUrl.value);
    avatarPreviewUrl.value = null;
  }

  selectedAvatarFile.value = file;
  avatarDeleteRequested.value = false;

  const preview = URL.createObjectURL(file);
  avatarPreviewUrl.value = preview;
  avatarUrl.value = preview;
}

function handleRemoveAvatar() {
  selectedAvatarFile.value = null;

  if (avatarPreviewUrl.value) {
    URL.revokeObjectURL(avatarPreviewUrl.value);
    avatarPreviewUrl.value = null;
  }

  avatarUrl.value = null;

  if (originalAvatarUrl.value) {
    avatarDeleteRequested.value = true;
  }
}

/* ========== Actions ========== */

async function handleSaveProfile() {
  if (isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
    return;
  }

  try {
    savingProfile.value = true;

    // 1) อัปเดตชื่อ (ถ้าเปลี่ยน)
    if (profileName.value !== auth.displayName) {
      const res = await updateProfile(auth.token, {
        displayName: profileName.value,
      });

      auth.setAuth(res);

      profileName.value = auth.displayName || profileName.value;

      avatarUrl.value = auth.profileImageUrl;
      originalAvatarUrl.value = auth.profileImageUrl;
    }

    // 2) ถ้ามีไฟล์ใหม่ → อัปโหลดรูปโปรไฟล์
    if (selectedAvatarFile.value) {
      const res = await uploadProfilePicture(
        auth.token,
        selectedAvatarFile.value
      );

      auth.setAuth(res);

      avatarUrl.value = auth.profileImageUrl;
      originalAvatarUrl.value = auth.profileImageUrl;

      if (avatarPreviewUrl.value) {
        URL.revokeObjectURL(avatarPreviewUrl.value);
        avatarPreviewUrl.value = null;
      }
      selectedAvatarFile.value = null;
      avatarDeleteRequested.value = false;
    }
    // 3) ถ้า mark ว่าจะลบรูปเดิม
    else if (avatarDeleteRequested.value && originalAvatarUrl.value) {
      await deleteProfilePicture(auth.token);

      auth.setProfileImageUrl(null);
      avatarUrl.value = null;
      originalAvatarUrl.value = null;
      avatarDeleteRequested.value = false;
    }

    toast.success("บันทึกโปรไฟล์เรียบร้อย");
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }

    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "บันทึกโปรไฟล์ไม่สำเร็จ";
    toast.error(msg);
  } finally {
    savingProfile.value = false;
  }
}

/* เปลี่ยนรหัสผ่าน */
async function handleChangePassword() {
  passwordError.value = "";

  if (isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
    return;
  }

  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = "รหัสผ่านใหม่และการยืนยันไม่ตรงกัน";
    return;
  }

  if (newPassword.value.length < 6) {
    passwordError.value = "รหัสผ่านใหม่ต้องมีอย่างน้อย 6 ตัวอักษร";
    return;
  }

  try {
    changingPassword.value = true;
    await changePassword(auth.token, {
      currentPassword: currentPassword.value,
      newPassword: newPassword.value,
    });

    toast.success("เปลี่ยนรหัสผ่านสำเร็จ 🎉");

    currentPassword.value = "";
    newPassword.value = "";
    confirmPassword.value = "";
    passwordError.value = "";
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }

    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "เปลี่ยนรหัสผ่านไม่สำเร็จ";
    passwordError.value = msg;
    toast.error(msg);
  } finally {
    changingPassword.value = false;
  }
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