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
        กรอกข้อมูลสถานที่เที่ยวที่คุณอยากแชร์ แล้วกดบันทึก ระบบจะบันทึกทริปนี้ไว้ใน
        Dashboard ของคุณ
      </p>

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

        <!-- Places -->
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
            class="w-full border rounded-md px-3 py-2 text-sm resize-none focus:outline-none focus:ring-1 focus:ring-sky-500"
            placeholder="เล่าบรรยากาศ สถานที่ที่แนะนำ ไฮไลต์ของทริปนี้ ฯลฯ"
          />
          <p class="text-xs text-gray-400 mt-1">(ไม่เกิน 1000 ตัวอักษร)</p>
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
          </p>
        </div>

        <!-- Latitude / Longitude -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">
              Latitude
            </label>
            <input
              v-model="latitude"
              type="number"
              step="0.000001"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              placeholder="เช่น 66.5039"
            />
          </div>

          <div>
            <label class="block text-sm font-medium mb-1">
              Longitude
            </label>
            <input
              v-model="longitude"
              type="number"
              step="0.000001"
              class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
              placeholder="เช่น 25.7294"
            />
          </div>
        </div>

        <!-- Error message -->
        <p v-if="error" class="text-sm text-red-500">
          {{ error }}
        </p>

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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import { createTrip, type TripPayload } from "@/services/tripService";

// Toast
import { useToast } from "vue-toastification";
const toast = useToast();

const router = useRouter();
const auth = useAuthStore();

const title = ref("");
const province = ref("");
const description = ref("");

// new fields
const tagsInput = ref("");
const latitude = ref("");
const longitude = ref("");

const loading = ref(false);
const error = ref("");

async function handleSubmit() {
  error.value = "";

  if (!auth.token) {
    toast.error("กรุณาเข้าสู่ระบบก่อนสร้างทริปใหม่");
    return;
  }

  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  if (!titleTrim) {
    toast.warning("กรุณากรอกชื่อทริป");
    return;
  }
  if (!provinceTrim) {
    toast.warning("กรุณากรอกสถานที่");
    return;
  }

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
    await createTrip(auth.token, payload);

    toast.success("บันทึกทริปเรียบร้อยแล้ว 🎉");

    router.push({ name: "dashboard" });
  } catch (err: any) {
    console.error(err);
    const message = err.message || "บันทึกทริปไม่สำเร็จ";
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