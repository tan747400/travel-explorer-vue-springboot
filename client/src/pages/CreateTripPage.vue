<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-xl mx-auto px-4 py-10">
      <!-- Heading -->
      <div class="mb-6 flex items-center justify-between">
        <h1 class="text-2xl md:text-3xl font-bold">
          เพิ่มทริปใหม่
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
        กรอกข้อมูลสถานที่เที่ยวที่คุณอยากแชร์ แล้วกดบันทึก
        ระบบจะบันทึกทริปนี้ไว้ใน Dashboard ของคุณ
      </p>

      <!-- Card ฟอร์ม / Skeleton ตอนกำลังบันทึก -->
      <section
        v-if="!loading"
        class="bg-white rounded-2xl border border-slate-200 shadow-sm px-4 sm:px-6 py-5"
      >
        <!-- ฟอร์มสร้างทริป -->
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
              placeholder="เช่น เที่ยวเชียงใหม่หน้าหนาว"
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
              placeholder="เช่น เชียงใหม่ ฟินแลนด์"
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
              placeholder="เล่าบรรยากาศ สถานที่ที่แนะนำ ไฮไลต์ของทริปนี้ ฯลฯ"
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
              แท็ก (คั่นด้วยเครื่องหมายจุลภาค ,)
            </label>
            <input
              v-model="tagsInput"
              type="text"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              placeholder="เช่น ธรรมชาติ, ภูเขา, หน้าหนาว"
            />
            <p class="text-xs text-gray-400 mt-1">
              ใช้สำหรับค้นหา / แสดงเป็นป้ายกำกับ เช่น “ทะเล”, “ธรรมชาติ”
              (ไม่เกิน 10 แท็ก แต่ละแท็กไม่เกิน 30 ตัวอักษร)
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
                placeholder="เช่น 19.823787130277466"
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
                placeholder="เช่น 99.76391418182524"
              />
            </div>
          </div>

          <!-- รูปภาพทริป -->
          <div class="pt-1">
            <label class="block text-sm font-medium mb-1">
              รูปภาพทริป (อัปโหลดได้หลายรูป, ไม่บังคับ)
            </label>

            <input
              type="file"
              multiple
              accept="image/*"
              class="block w-full text-sm text-gray-700 file:mr-3 file:py-2 file:px-4
                     file:rounded-md file:border-0 file:text-sm file:font-medium
                     file:bg-sky-50 file:text-sky-700 hover:file:bg-sky-100"
              @change="handleFilesChange"
            />

            <p class="text-xs text-gray-400 mt-1">
              รองรับไฟล์รูปภาพทั่วไป เช่น .jpg, .png, .webp
            </p>

            <!-- Preview -->
            <div
              v-if="previewUrls.length > 0"
              class="mt-3 grid grid-cols-3 gap-2"
            >
              <div
                v-for="(url, idx) in previewUrls"
                :key="idx"
                class="relative"
              >
                <img
                  :src="url"
                  alt="preview"
                  class="h-24 w-full rounded-md object-cover border"
                />
              </div>
            </div>
          </div>

          <!-- Error -->
          <div
            v-if="error"
            class="rounded-lg bg-red-50 border border-red-200 px-3 py-2 text-sm text-red-700"
          >
            {{ error }}
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-3 pt-2">
            <button
              type="submit"
              class="px-4 py-2 rounded-md bg-sky-600 text-white text-sm font-medium hover:bg-sky-700 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="loading"
            >
              {{ loading ? "กำลังบันทึก..." : "บันทึกทริป" }}
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

      <!-- Skeleton ตอนกำลังบันทึก -->
      <CreateTripSkeleton v-else />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, onMounted } from "vue";
import { useRouter } from "vue-router";

import { useAuthStore } from "@/stores/authStore";
import {
  createTrip,
  uploadTripPhotos,
  type TripPayload,
} from "@/services/tripService";

import { useToast } from "vue-toastification";
import CreateTripSkeleton from "@/components/state/CreateTripSkeleton.vue";
import { useSessionExpired } from "@/composables/useSessionExpired";

const toast = useToast();
const router = useRouter();
const auth = useAuthStore();
const { handleSessionExpired } = useSessionExpired();

const title = ref("");
const province = ref("");
const description = ref("");

// tags / lat / lng
const tagsInput = ref("");
const latitude = ref("");
const longitude = ref("");

// files + preview
const selectedFiles = ref<File[]>([]);
const previewUrls = ref<string[]>([]);

const loading = ref(false);
const error = ref("");

/* ---------- helper: ตรวจ token ---------- */
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

    if (!payload.exp) return false; // ไม่มี exp ให้ไปลุ้น 401 จาก backend

    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
  } catch {
    return true;
  }
}

/* ตอนเข้าเพจ: ถ้า token พัง/หมดอายุ ให้เด้งออกเลย */
onMounted(() => {
  if (isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
  }
});

// helper: จัดการ preview URLs
function clearPreviews() {
  previewUrls.value.forEach((url) => URL.revokeObjectURL(url));
  previewUrls.value = [];
}

function handleFilesChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const files = input.files ? Array.from(input.files) : [];

  selectedFiles.value = files;

  // เคลียร์ URL เก่า
  clearPreviews();

  // สร้าง URL ใหม่
  previewUrls.value = files.map((file) => URL.createObjectURL(file));
}

onBeforeUnmount(() => {
  clearPreviews();
});

// validation
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

async function handleSubmit() {
  error.value = "";

  // เช็ค token ก่อน submit
  if (isTokenInvalidOrExpired(auth.token)) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  if (!validateForm()) return;

  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  const tags =
    tagsInput.value
      .split(",")
      .map((t) => t.trim())
      .filter((t) => t.length > 0) || [];

  const latNum = latitude.value ? Number(latitude.value) : null;
  const lngNum = longitude.value ? Number(longitude.value) : null;

  const payload: TripPayload = {
    title: titleTrim,
    province: provinceTrim,
    description: descriptionTrim || null,
    tags: tags.length > 0 ? tags : null,
    latitude: latNum !== null && !Number.isNaN(latNum) ? latNum : null,
    longitude: lngNum !== null && !Number.isNaN(lngNum) ? lngNum : null,
  };

  loading.value = true;

  try {
    // 1) สร้างทริป
    const trip = await createTrip(auth.token, payload);

    // 2) อัปโหลดรูป ถ้ามี
    if (selectedFiles.value.length > 0) {
      try {
        await uploadTripPhotos(trip.id, auth.token, selectedFiles.value);
      } catch (uploadErr: any) {
        console.error(uploadErr);
        toast.warning("บันทึกทริปแล้ว แต่บางรูปอัปโหลดไม่สำเร็จ");
      }
    }

    toast.success("บันทึกทริปเรียบร้อยแล้ว 🎉");
    router.push({ name: "dashboard" });
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }

    if (err?.response?.status === 403) {
      const msg = "คุณสามารถสร้าง/แก้ไขทริปได้เมื่อเข้าสู่ระบบเท่านั้น";
      error.value = msg;
      toast.error(msg);
      return;
    }

    const message =
      err?.response?.data?.message || err.message || "บันทึกทริปไม่สำเร็จ";
    toast.error(message);
    error.value = message;
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push({ name: "dashboard" });
}
</script>