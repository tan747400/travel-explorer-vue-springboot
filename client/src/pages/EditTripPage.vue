<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-xl mx-auto px-4 py-10">
      <!-- Header -->
      <div class="mb-6 flex items-center justify-between">
        <h1 class="text-2xl md:text-3xl font-bold">
          แก้ไขทริป
        </h1>

        <button
          type="button"
          class="text-sm text-sky-600 hover:underline"
          @click="goBack"
        >
          ← กลับไป Dashboard
        </button>
      </div>

      <p class="text-gray-600 mb-6 text-sm">
        ปรับแก้ข้อมูลสถานที่เที่ยวของคุณ แล้วกดบันทึกเพื่ออัปเดตทริป
      </p>

      <!-- Loading ตอนโหลดข้อมูลครั้งแรก: ใช้ Skeleton -->
      <EditTripSkeleton v-if="loading && !trip" />

      <!-- Error ตอนโหลดข้อมูลครั้งแรก -->
      <p v-else-if="!trip && error" class="text-sm text-red-500 mb-4">
        {{ error }}
      </p>

      <!-- Form -->
      <section
        v-else
        class="bg-white rounded-2xl border border-slate-200 shadow-sm px-4 sm:px-6 py-5"
      >
        <form class="space-y-4" @submit.prevent="handleSubmit">
          <!-- Title -->
          <div>
            <label class="block text-sm font-medium mb-1">
              ชื่อทริป / สถานที่<span class="text-red-500">*</span>
            </label>
            <input
              v-model="title"
              type="text"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              required
            />
          </div>

          <!-- Province -->
          <div>
            <label class="block text-sm font-medium mb-1">
              สถานที่<span class="text-red-500">*</span>
            </label>
            <input
              v-model="province"
              type="text"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              required
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium mb-1">
              รายละเอียดทริป
            </label>
            <textarea
              v-model="description"
              rows="4"
              maxlength="1000"
              class="w-full border rounded-md px-3 py-2 text-sm resize-none focus:outline-none focus:ring-1 focus:ring-sky-500"
            />
            <div class="mt-1 flex items-center justify-between">
              <p class="text-xs text-gray-400">
                (ไม่เกิน 1000 ตัวอักษร)
              </p>
              <p class="text-[11px] text-gray-400">
                {{ description.length }}/1000
              </p>
            </div>
          </div>

          <!-- Tags -->
          <div>
            <label class="block text-sm font-medium mb-1">
              แท็ก (คั่นด้วย ,)
            </label>
            <input
              v-model="tagsInput"
              type="text"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              placeholder="ธรรมชาติ, ภูเขา, หน้าหนาว"
            />
            <p class="text-xs text-gray-400 mt-1">
              ไม่เกิน 10 แท็ก แต่ละแท็กไม่เกิน 30 ตัวอักษร
            </p>
          </div>

          <!-- Lat / Lng -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium mb-1">
                Latitude
              </label>
              <input
                v-model="latitude"
                type="number"
                step="any"
                inputmode="decimal"
                class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">
                Longitude
              </label>
              <input
                v-model="longitude"
                type="number"
                step="any"
                inputmode="decimal"
                class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              />
            </div>
          </div>

          <!-- Map preview -->
          <div
            v-if="hasLocation"
            class="mt-2 rounded-xl overflow-hidden border bg-white"
          >
            <iframe
              :src="mapEmbedUrl"
              width="100%"
              height="220"
              style="border:0;"
              allowfullscreen
              loading="lazy"
              referrerpolicy="no-referrer-when-downgrade"
            ></iframe>
          </div>

          <!-- รูปภาพทริป / อัปโหลดรูปเพิ่ม / ลบรูป -->
          <section class="mt-6 border-t border-slate-200 pt-4">
            <h2 class="text-sm font-semibold mb-3">
              รูปภาพทริป
            </h2>

            <!-- แสดงรูปที่มีอยู่แล้ว + ปุ่มลบรูป -->
            <div
              v-if="trip && trip.photos && trip.photos.length"
              class="flex flex-wrap gap-3 mb-4"
            >
              <div
                v-for="url in trip.photos"
                :key="url"
                class="relative group"
              >
                <img
                  :src="url"
                  :alt="trip.title"
                  class="w-28 h-20 object-cover rounded-lg border"
                />

                <!-- ปุ่มลบรูป -->
                <button
                  type="button"
                  class="absolute inset-x-1 bottom-1 px-2 py-0.5 rounded-md text-[11px]
                         bg-black/65 text-white opacity-0 group-hover:opacity-100
                         transition-opacity flex items-center justify-center gap-1"
                  @click="openDeletePhotoModal(url)"
                >
                  <span>ลบรูป</span>
                </button>
              </div>
            </div>
            <p v-else class="text-xs text-gray-500 mb-3">
              ยังไม่มีรูปภาพในทริปนี้ ลองอัปโหลดรูปดูนะ
            </p>

            <!-- อินพุตเลือกไฟล์ + ปุ่มอัปโหลด -->
            <div class="flex flex-col gap-2 max-w-md">
              <input
                ref="fileInputRef"
                type="file"
                multiple
                accept="image/*"
                @change="onFilesSelected"
                class="block w-full text-sm text-gray-700"
              />

              <button
                type="button"
                class="inline-flex items-center justify-center px-4 py-2 rounded-lg
                       bg-sky-600 text-white text-sm font-medium
                       hover:bg-sky-700 disabled:opacity-60 disabled:cursor-not-allowed"
                :disabled="uploadingPhotos || !uploadFiles.length"
                @click="handleUploadPhotos"
              >
                <span v-if="uploadingPhotos">กำลังอัปโหลดรูป...</span>
                <span v-else>อัปโหลดรูปเพิ่ม</span>
              </button>

              <p v-if="uploadError" class="text-xs text-red-500">
                {{ uploadError }}
              </p>

              <p v-if="uploadFiles.length" class="text-[11px] text-slate-500">
                เลือกรูปแล้ว {{ uploadFiles.length }} ไฟล์
              </p>
            </div>
          </section>

          <!-- Error -->
          <div
            v-if="error"
            class="rounded-lg bg-red-50 border border-red-200 px-3 py-2 text-sm text-red-700"
          >
            {{ error }}
          </div>

          <!-- Buttons -->
          <div class="flex items-center gap-3 pt-2">
            <button
              type="submit"
              class="px-4 py-2 rounded-md bg-sky-600 text-white text-sm hover:bg-sky-700 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="loading"
            >
              {{ loading ? "กำลังบันทึก..." : "บันทึกการเปลี่ยนแปลง" }}
            </button>

            <button
              type="button"
              class="px-4 py-2 rounded-md border text-sm text-gray-600 hover:bg-gray-50"
              @click="goBack"
            >
              ยกเลิก
            </button>
          </div>
        </form>
      </section>
    </div>

    <!-- Popup ลบรูป -->
    <Transition name="fade">
      <div
        v-if="showDeletePhotoModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
      >
        <div class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
          <h3 class="text-lg font-semibold text-slate-800 text-center">
            ต้องการลบรูปนี้ออกจากทริปจริง ๆ ไหม?
          </h3>
          <p class="mt-2 text-sm text-slate-500 text-center">
            รูปจะถูกลบออกจากทริปของคุณ และไม่สามารถกู้คืนได้
          </p>

          <div class="mt-6 flex items-center justify-center gap-3">
            <button
              type="button"
              class="px-4 py-2 rounded-lg border text-slate-600 hover:bg-slate-50"
              @click="closeDeletePhotoModal"
              :disabled="deletingPhoto"
            >
              ยกเลิก
            </button>

            <button
              type="button"
              class="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="deletingPhoto"
              @click="confirmDeletePhoto"
            >
              {{ deletingPhoto ? "กำลังลบรูป..." : "ลบรูป" }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";

import { useAuthStore } from "@/stores/authStore";
import {
  getTripById,
  updateTrip,
  uploadTripPhotos,
  deleteTripPhoto,
} from "@/services/tripService";
import type { Trip } from "@/types/trip";

import EditTripSkeleton from "@/components/state/EditTripSkeleton.vue";
import { useToast } from "vue-toastification";
import { useSessionExpired } from "@/composables/useSessionExpired";

const toast = useToast();

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const { handleSessionExpired } = useSessionExpired();

const tripId = Number(route.params.id);

// state ทริปเต็ม ๆ (ไว้ดูรูป)
const trip = ref<Trip | null>(null);

const title = ref("");
const province = ref("");
const description = ref("");
const tagsInput = ref("");
const latitude = ref("");
const longitude = ref("");

const loading = ref(false);
const error = ref("");

// สำหรับอัปโหลดรูป
const uploadFiles = ref<File[]>([]);
const uploadingPhotos = ref(false);
const uploadError = ref("");
const fileInputRef = ref<HTMLInputElement | null>(null);

// ลบรูปเดิม
const showDeletePhotoModal = ref(false);
const photoToDelete = ref<string | null>(null);
const deletingPhoto = ref(false);

// helper ตรวจ token
function isTokenInvalidOrExpired(token: string | null | undefined): boolean {
  if (!token) return true;

  try {
    const parts = token.split(".");
    if (parts.length !== 3) return true;

    const payloadPart = parts[1];
    if (!payloadPart) return true;

    const payloadBase64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const json = atob(payloadBase64);
    const payload = JSON.parse(json) as { exp?: number };

    if (!payload.exp) return false; // ไม่มี exp ก็ให้ไปลุ้น 401 จาก backend

    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
  } catch {
    return true;
  }
}

// มีพิกัดไหม
const hasLocation = computed(() => {
  if (!latitude.value || !longitude.value) return false;
  const lat = Number(latitude.value);
  const lng = Number(longitude.value);
  return !Number.isNaN(lat) && !Number.isNaN(lng);
});

// URL สำหรับ Google Maps Embed
const mapEmbedUrl = computed(() => {
  if (!hasLocation.value) return "";
  const lat = Number(latitude.value);
  const lng = Number(longitude.value);
  return `https://www.google.com/maps?q=${lat},${lng}&z=14&output=embed`;
});

// validation ฟอร์ม
function validateForm(): boolean {
  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  if (titleTrim.length < 3) {
    const msg = "ชื่อทริปต้องมีอย่างน้อย 3 ตัวอักษร";
    error.value = msg;
    toast.warning(msg);
    return false;
  }
  if (provinceTrim.length < 2) {
    const msg = "กรุณากรอกชื่อสถานที่ให้ถูกต้อง";
    error.value = msg;
    toast.warning(msg);
    return false;
  }
  if (descriptionTrim.length > 1000) {
    const msg = "รายละเอียดต้องไม่เกิน 1000 ตัวอักษร";
    error.value = msg;
    toast.warning(msg);
    return false;
  }

  if (tagsInput.value.trim()) {
    const rawTags = tagsInput.value
      .split(",")
      .map((t) => t.trim())
      .filter((t) => t.length > 0);

    if (rawTags.length > 10) {
      const msg = "แท็กต้องไม่เกิน 10 แท็ก";
      error.value = msg;
      toast.warning(msg);
      return false;
    }

    if (rawTags.some((t) => t.length > 30)) {
      const msg = "แต่ละแท็กต้องไม่เกิน 30 ตัวอักษร";
      error.value = msg;
      toast.warning(msg);
      return false;
    }
  }

  if (latitude.value) {
    const lat = Number(latitude.value);
    if (Number.isNaN(lat) || lat < -90 || lat > 90) {
      const msg = "Latitude ต้องอยู่ระหว่าง -90 ถึง 90";
      error.value = msg;
      toast.warning(msg);
      return false;
    }
  }

  if (longitude.value) {
    const lng = Number(longitude.value);
    if (Number.isNaN(lng) || lng < -180 || lng > 180) {
      const msg = "Longitude ต้องอยู่ระหว่าง -180 ถึง 180";
      error.value = msg;
      toast.warning(msg);
      return false;
    }
  }

  return true;
}

// โหลดข้อมูลทริป
async function loadTrip() {
  try {
    loading.value = true;
    const loaded = await getTripById(tripId);
    trip.value = loaded;

    title.value = loaded.title || "";
    province.value = loaded.province || "";
    description.value = loaded.description || "";
    tagsInput.value = loaded.tags ? loaded.tags.join(", ") : "";
    latitude.value = loaded.latitude != null ? String(loaded.latitude) : "";
    longitude.value =
      loaded.longitude != null ? String(loaded.longitude) : "";
  } catch (err: any) {
    console.error(err);
    const message =
      err?.response?.data?.message || err.message || "โหลดข้อมูลทริปไม่สำเร็จ";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
}

// ตอนเข้าเพจ: ถ้า token พัง/หมดอายุ → เด้งออกเลย
onMounted(async () => {
  if (isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
    return;
  }

  await loadTrip();
});

async function handleSubmit() {
  error.value = "";

  if (isTokenInvalidOrExpired(auth.token)) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  if (!validateForm()) return;

  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  const tags = tagsInput.value
    .split(",")
    .map((t) => t.trim())
    .filter((t) => t.length > 0);

  try {
    loading.value = true;

    const updated = await updateTrip(tripId, auth.token, {
      title: titleTrim,
      province: provinceTrim,
      description: descriptionTrim || null,
      tags: tags.length > 0 ? tags : null,
      latitude: latitude.value ? Number(latitude.value) : null,
      longitude: longitude.value ? Number(longitude.value) : null,
    });

    trip.value = updated;

    toast.success("แก้ไขทริปสำเร็จ 🎉");
    router.push({ name: "trip-detail", params: { id: tripId } });
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }
    if (err?.response?.status === 403) {
      const msg = "คุณสามารถแก้ไขทริปที่คุณสร้างเองเท่านั้น";
      error.value = msg;
      toast.error(msg);
      return;
    }

    const message =
      err?.response?.data?.message || err.message || "เกิดข้อผิดพลาดในการบันทึกทริป";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push({ name: "dashboard" });
}

// เมื่อผู้ใช้เลือกไฟล์รูป
function onFilesSelected(event: Event) {
  uploadError.value = "";
  const input = event.target as HTMLInputElement;
  const files = input.files ? Array.from(input.files) : [];
  uploadFiles.value = files;

  if (!files.length) {
    uploadError.value = "";
  }
}

// กดปุ่มอัปโหลดรูปเพิ่ม
async function handleUploadPhotos() {
  if (!trip.value) return;

  if (!uploadFiles.value.length) {
    uploadError.value = "กรุณาเลือกรูปก่อนค่ะ";
    toast.warning(uploadError.value);
    return;
  }

  if (isTokenInvalidOrExpired(auth.token)) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  try {
    uploadingPhotos.value = true;

    // เรียก API อัปโหลดรูป
    await uploadTripPhotos(tripId, auth.token, uploadFiles.value);

    // รีโหลดทริปอีกรอบ เพื่อให้รูปใหม่ขึ้นแน่นอน
    await loadTrip();

    // เคลียร์ไฟล์ / input
    uploadFiles.value = [];
    uploadError.value = "";
    if (fileInputRef.value) {
      fileInputRef.value.value = "";
    }

    toast.success("อัปโหลดรูปสำเร็จแล้ว 🎉");
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }

    const message =
      err?.response?.data?.message || err.message || "อัปโหลดรูปไม่สำเร็จ";
    uploadError.value = message;
    toast.error(message);
  } finally {
    uploadingPhotos.value = false;
  }
}

/* ====== ลบรูปเดิม ====== */
function openDeletePhotoModal(url: string) {
  photoToDelete.value = url;
  showDeletePhotoModal.value = true;
}

function closeDeletePhotoModal() {
  if (deletingPhoto.value) return;
  showDeletePhotoModal.value = false;
  photoToDelete.value = null;
}

async function confirmDeletePhoto() {
  if (!trip.value || !photoToDelete.value) return;

  if (isTokenInvalidOrExpired(auth.token)) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  try {
    deletingPhoto.value = true;

    await deleteTripPhoto(tripId, photoToDelete.value, auth.token);
    await loadTrip();

    toast.success("ลบรูปออกจากทริปแล้ว");
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }

    const message =
      err?.response?.data?.message || err.message || "ลบรูปไม่สำเร็จ";
    uploadError.value = message;
    toast.error(message);
  } finally {
    deletingPhoto.value = false;
    closeDeletePhotoModal();
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>