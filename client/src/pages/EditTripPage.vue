<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-xl mx-auto px-4 py-10">
      <!-- Header -->
      <div class="mb-6 flex items-center justify-between">
        <h1 class="text-2xl md:text-3xl font-bold">แก้ไขทริป</h1>

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

      <!-- Form -->
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
            class="w-full border rounded-md px-3 py-2 text-sm resize-none focus:outline-none focus:ring-1 focus:ring-sky-500"
          />
          <p class="text-xs text-gray-400 mt-1">
            (ไม่เกิน 1000 ตัวอักษร)
          </p>
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
        </div>

        <!-- Lat / Lng -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Latitude</label>
            <input
              v-model="latitude"
              type="number"
              step="0.000001"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium mb-1">Longitude</label>
            <input
              v-model="longitude"
              type="number"
              step="0.000001"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
            />
          </div>
        </div>

        <!-- Map preview -->
        <div
          v-if="hasLocation"
          class="mt-4 rounded-xl overflow-hidden border bg-white"
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

        <!-- รูปภาพทริป / อัปโหลดรูปเพิ่ม -->
        <section class="mt-6 border-t border-slate-200 pt-4">
          <h2 class="text-sm font-semibold mb-3">
            รูปภาพทริป
          </h2>

          <!-- แสดงรูปที่มีอยู่แล้ว -->
          <div
            v-if="trip && trip.photos && trip.photos.length"
            class="flex flex-wrap gap-3 mb-4"
          >
            <img
              v-for="url in trip.photos"
              :key="url"
              :src="url"
              :alt="trip.title"
              class="w-28 h-20 object-cover rounded-lg border"
            />
          </div>
          <p
            v-else
            class="text-xs text-gray-500 mb-3"
          >
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
          </div>
        </section>

        <!-- Error -->
        <p v-if="error" class="text-sm text-red-500">{{ error }}</p>

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
    </div>
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
} from "@/services/tripService";
import type { Trip } from "@/types/trip";

// Toast
import { useToast } from "vue-toastification";
const toast = useToast();

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

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

// ฟังก์ชันใช้ซ้ำตอน token หมดอายุ
function goLoginExpired() {
  auth.logout();
  toast.error("เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");

  router.push({
    name: "login",
    query: {
      expired: "1",
      redirect: router.currentRoute.value.fullPath,
    },
  });
}

// validation พื้นฐาน
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

onMounted(async () => {
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
    const message = err.message || "โหลดข้อมูลทริปไม่สำเร็จ";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
});

async function handleSubmit() {
  error.value = "";

  if (!auth.token) {
    const message = "ไม่พบโทเคน กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    error.value = message;
    toast.error(message);
    goLoginExpired();
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

    trip.value = updated; // sync state ทริป

    toast.success("แก้ไขทริปสำเร็จ 🎉");
    router.push({ name: "dashboard" });
  } catch (err: any) {
    console.error(err);

    if (err?.status === 401) {
      goLoginExpired();
      return;
    }
    if (err?.status === 403) {
      const msg = "คุณสามารถแก้ไขทริปที่คุณสร้างเองเท่านั้น";
      error.value = msg;
      toast.error(msg);
      return;
    }

    const message = err.message || "เกิดข้อผิดพลาดในการบันทึกทริป";
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

  if (!auth.token) {
    const message = "ไม่พบโทเคน กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    error.value = message;
    toast.error(message);
    goLoginExpired();
    return;
  }

  try {
    uploadingPhotos.value = true;
    const updated = await uploadTripPhotos(
      tripId,
      auth.token,
      uploadFiles.value
    );
    trip.value = updated;
    uploadFiles.value = [];
    uploadError.value = "";

    // ล้าง input file จริง ๆ
    if (fileInputRef.value) {
      fileInputRef.value.value = "";
    }

    toast.success("อัปโหลดรูปสำเร็จแล้ว 🎉");
  } catch (err: any) {
    console.error(err);

    if (err?.status === 401) {
      goLoginExpired();
      return;
    }

    const message = err.message || "อัปโหลดรูปไม่สำเร็จ";
    uploadError.value = message;
    toast.error(message);
  } finally {
    uploadingPhotos.value = false;
  }
}
</script>