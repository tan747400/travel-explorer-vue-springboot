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
import { getTripById, updateTrip } from "@/services/tripService";

// Toast
import { useToast } from "vue-toastification";
const toast = useToast();

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const title = ref("");
const province = ref("");
const description = ref("");
const tagsInput = ref("");
const latitude = ref("");
const longitude = ref("");

const loading = ref(false);
const error = ref("");

const tripId = Number(route.params.id);

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

onMounted(async () => {
  try {
    loading.value = true;
    const trip = await getTripById(tripId);

    title.value = trip.title || "";
    province.value = trip.province || "";
    description.value = trip.description || "";
    tagsInput.value = trip.tags ? trip.tags.join(", ") : "";
    latitude.value = trip.latitude != null ? String(trip.latitude) : "";
    longitude.value = trip.longitude != null ? String(trip.longitude) : "";
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

  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  if (!titleTrim) {
    const message = "กรุณากรอกชื่อทริป";
    error.value = message;
    toast.warning(message);
    return;
  }
  if (!provinceTrim) {
    const message = "กรุณากรอกสถานที่";
    error.value = message;
    toast.warning(message);
    return;
  }

  const tags = tagsInput.value
    .split(",")
    .map((t) => t.trim())
    .filter((t) => t.length > 0);

  try {
    loading.value = true;

    await updateTrip(tripId, auth.token, {
      title: titleTrim,
      province: provinceTrim,
      description: descriptionTrim || null,
      tags: tags.length > 0 ? tags : null,
      latitude: latitude.value ? Number(latitude.value) : null,
      longitude: longitude.value ? Number(longitude.value) : null,
    });

    toast.success("แก้ไขทริปสำเร็จ 🎉");
    router.push({ name: "dashboard" });
  } catch (err: any) {
    console.error(err);

    if (err?.status === 401) {
      // token หมดอายุ
      goLoginExpired();
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
</script>