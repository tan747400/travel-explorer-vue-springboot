<template>
  <!-- ใช้ h-screen + flex + overflow-hidden กันการ scroll -->
  <div class="h-screen bg-slate-50 flex overflow-hidden">
    <main class="w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-10 py-10">
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

      <template v-else>
        <!-- Header: avatar + ชื่อ -->
        <header class="mb-8 flex items-center gap-4">
          <div
            class="h-14 w-14 sm:h-16 sm:w-16 rounded-full bg-gradient-to-br from-sky-500 to-sky-600 flex items-center justify-center text-white text-2xl font-semibold shadow-md overflow-hidden"
          >
            <!-- ถ้ามีรูปให้โชว์รูป ไม่งั้นใช้ตัวอักษร -->
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

          <div class="flex flex-col">
            <h1 class="text-2xl sm:text-3xl font-bold text-slate-900">
              {{ profileName || "ผู้ใช้งาน" }}
            </h1>
            <p class="text-sm text-slate-500">
              {{ auth.userEmail }}
            </p>
          </div>
        </header>

        <div class="flex flex-col md:flex-row gap-8">
          <!-- ซ้าย: เมนูแท็บ -->
          <aside class="md:w-52 flex-shrink-0">
            <nav
              class="rounded-2xl bg-white border border-slate-200 shadow-sm py-4"
            >
              <button
                type="button"
                class="w-full flex items-center gap-3 px-4 py-2.5 text-sm"
                :class="
                  activeTab === 'profile'
                    ? 'text-slate-900 font-semibold bg-slate-50 border-r-4 border-sky-500'
                    : 'text-slate-600 hover:bg-slate-50'
                "
                @click="activeTab = 'profile'"
              >
                <span class="text-lg">👤</span>
                <span>Profile</span>
              </button>

              <button
                type="button"
                class="w-full flex items-center gap-3 px-4 py-2.5 text-sm"
                :class="
                  activeTab === 'password'
                    ? 'text-slate-900 font-semibold bg-slate-50 border-r-4 border-sky-500'
                    : 'text-slate-600 hover:bg-slate-50'
                "
                @click="activeTab = 'password'"
              >
                <span class="text-lg">🔒</span>
                <span>Reset password</span>
              </button>
            </nav>
          </aside>

          <!-- ขวา: เนื้อหาแต่ละแท็บ -->
          <section
            class="flex-1 rounded-3xl bg-slate-50 border border-slate-200 shadow-sm px-6 sm:px-8 py-6 sm:py-8"
          >
            <!-- Tab: Profile -->
            <div v-if="activeTab === 'profile'" class="space-y-6">
              <!-- Avatar + upload -->
              <div class="flex flex-col sm:flex-row sm:items-center gap-6">
                <div class="relative">
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
                    class="absolute -top-1 -right-1 h-7 w-7 rounded-full bg-white border border-slate-200 shadow flex items-center justify-center text-slate-500 hover:bg-slate-50 text-xs"
                    @click="handleRemoveAvatar"
                    aria-label="ลบรูปโปรไฟล์"
                  >
                    ✕
                  </button>
                </div>

                <div class="flex flex-col gap-2">
                  <button
                    type="button"
                    class="inline-flex items-center justify-center px-4 py-2 rounded-full border border-slate-300 bg-white text-sm text-slate-700 hover:bg-slate-50 disabled:opacity-60 disabled:cursor-not-allowed"
                    @click="triggerUpload"
                    :disabled="uploadingAvatar"
                  >
                    <span v-if="uploadingAvatar">กำลังอัปโหลด...</span>
                    <span v-else>Upload profile picture</span>
                  </button>
                  <p class="text-xs text-slate-400">
                    รองรับไฟล์รูปภาพทั่วไป (เช่น .jpg, .png) ใช้เพื่อแสดงเฉพาะในฝั่ง UI
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
              <form
                class="mt-6 space-y-4"
                @submit.prevent="handleSaveProfile"
              >
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

              <form class="space-y-3" @submit.prevent="handleChangePassword">
                <div>
                  <label class="block text-xs font-medium text-slate-700 mb-1">
                    Current password
                  </label>
                  <input
                    v-model="currentPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="current-password"
                    required
                  />
                </div>

                <div>
                  <label class="block text-xs font-medium text-slate-700 mb-1">
                    New password
                  </label>
                  <input
                    v-model="newPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="new-password"
                    required
                  />
                </div>

                <div>
                  <label class="block text-xs font-medium text-slate-700 mb-1">
                    Confirm new password
                  </label>
                  <input
                    v-model="confirmPassword"
                    type="password"
                    class="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                    autocomplete="new-password"
                    required
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
                    <span v-if="changingPassword">กำลังเปลี่ยนรหัสผ่าน...</span>
                    <span v-else>Reset password</span>
                  </button>
                </div>
              </form>
            </div>
          </section>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from "vue";
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import { useToast } from "vue-toastification";
import {
  changePassword,
  uploadProfilePicture,
  deleteProfilePicture,
  updateProfile,
} from "@/services/authService";

const auth = useAuthStore();
const router = useRouter();
const toast = useToast();

/* ========== ปิด scroll ของ body ตอนอยู่หน้านี้ ========== */
let previousOverflow = "";

onMounted(() => {
  previousOverflow = document.body.style.overflow;
  document.body.style.overflow = "hidden";
});

onUnmounted(() => {
  document.body.style.overflow = previousOverflow;
});

/* ========== UI state ========== */
const activeTab = ref<"profile" | "password">("profile");

// avatar url มาจาก store
const avatarUrl = ref<string | null>(auth.profileImageUrl || null);
const uploadingAvatar = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

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

// Initials สำหรับ avatar
const initials = computed(() => {
  const base = profileName.value || auth.displayName || auth.userEmail || "";
  if (!base.trim()) return "?";

  const parts: string[] = base.trim().split(" ");
  const first = parts[0];
  if (!first) return "?";

  if (parts.length === 1) {
    return first.charAt(0).toUpperCase();
  }

  const second = parts[1] || "";
  return (first.charAt(0) + (second.charAt(0) || "")).toUpperCase();
});

/* ========== Avatar upload (จริง เชื่อม backend) ========== */
function triggerUpload() {
  fileInput.value?.click();
}

async function onAvatarSelected(e: Event) {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  if (!auth.token) {
    toast.error("เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
    auth.logout();
    router.push({ name: "login", query: { expired: "1" } });
    return;
  }

  try {
    uploadingAvatar.value = true;

    const res = await uploadProfilePicture(auth.token, file);

    avatarUrl.value = res.profileImageUrl || null;
    if ((auth as any).setProfileImageUrl) {
      (auth as any).setProfileImageUrl(res.profileImageUrl || null);
    } else {
      // fallback ถ้า store ยังไม่มี method นี้
      (auth as any).profileImageUrl = res.profileImageUrl || null;
    }

    if (res.displayName) {
      (auth as any).displayName = res.displayName;
      profileName.value = res.displayName;
    }

    toast.success("อัปโหลดรูปโปรไฟล์เรียบร้อย");
  } catch (err: any) {
    console.error(err);
    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "อัปโหลดรูปโปรไฟล์ไม่สำเร็จ";
    toast.error(msg);
  } finally {
    uploadingAvatar.value = false;
    if (fileInput.value) {
      fileInput.value.value = "";
    }
  }
}

async function handleRemoveAvatar() {
  if (!auth.token) {
    toast.error("เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
    auth.logout();
    router.push({ name: "login", query: { expired: "1" } });
    return;
  }

  try {
    await deleteProfilePicture(auth.token);

    avatarUrl.value = null;
    if ((auth as any).setProfileImageUrl) {
      (auth as any).setProfileImageUrl(null);
    } else {
      (auth as any).profileImageUrl = null;
    }

    if (fileInput.value) {
      fileInput.value.value = "";
    }

    toast.success("ลบรูปโปรไฟล์เรียบร้อย");
  } catch (err: any) {
    console.error(err);
    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "ลบรูปโปรไฟล์ไม่สำเร็จ";
    toast.error(msg);
  }
}

/* ========== Actions ========== */

async function handleSaveProfile() {
  if (!auth.token) {
    toast.error("เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
    auth.logout();
    router.push({ name: "login", query: { expired: "1" } });
    return;
  }

  try {
    savingProfile.value = true;
    const res = await updateProfile(auth.token, {
      displayName: profileName.value,
    });

    (auth as any).displayName = res.displayName;
    if ((auth as any).setDisplayName) {
      (auth as any).setDisplayName(res.displayName);
    }

    toast.success("บันทึกโปรไฟล์เรียบร้อย");
  } catch (err: any) {
    console.error(err);
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

  if (!auth.token) {
    toast.error("เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
    auth.logout();
    router.push({ name: "login", query: { expired: "1" } });
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
  } catch (err: any) {
    console.error(err);
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