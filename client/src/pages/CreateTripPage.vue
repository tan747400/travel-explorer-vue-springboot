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
            จังหวัด<span class="text-red-500">*</span>
          </label>
          <input
            v-model="province"
            type="text"
            class="w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-sky-500"
            placeholder="เช่น เชียงใหม่"
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
          <p class="text-xs text-gray-400 mt-1">
            (ไม่เกิน 1000 ตัวอักษร)
          </p>
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

const router = useRouter();
const auth = useAuthStore();

const title = ref("");
const province = ref("");
const description = ref("");
const loading = ref(false);
const error = ref("");

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function handleSubmit() {
  error.value = "";

  // กันกรณีไม่มี token (เผื่อหลุด session หรือเข้ามาทางแปลก ๆ)
  if (!auth.token) {
    error.value = "กรุณาเข้าสู่ระบบก่อนสร้างทริปใหม่";
    return;
  }

  // validation ฝั่ง frontend เพิ่มเติมจาก required
  const titleTrim = title.value.trim();
  const provinceTrim = province.value.trim();
  const descriptionTrim = description.value.trim();

  if (titleTrim.length === 0) {
    error.value = "กรุณากรอกชื่อทริป";
    return;
  }
  if (provinceTrim.length === 0) {
    error.value = "กรุณากรอกจังหวัด";
    return;
  }
  if (descriptionTrim.length > 1000) {
    error.value = "รายละเอียดต้องไม่เกิน 1000 ตัวอักษร";
    return;
  }

  loading.value = true;

  try {
    const res = await fetch(`${API_BASE_URL}/api/trips`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth.token}`,
      },
      body: JSON.stringify({
        title: titleTrim,
        province: provinceTrim,
        description: descriptionTrim || null,
      }),
    });

    if (res.status === 401) {
      throw new Error("เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง");
    }

    if (!res.ok) {
      let msg = "บันทึกทริปไม่สำเร็จ";
      try {
        const data = await res.json();
        if (data?.message) msg = data.message;
      } catch {
        // ถ้า parse ไม่ได้ ใช้ข้อความ default
      }
      throw new Error(msg);
    }

    alert("บันทึกทริปเรียบร้อยแล้ว");
    router.push({ name: "dashboard" });
  } catch (err: any) {
    error.value = err.message || "เกิดข้อผิดพลาดบางอย่าง";
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push({ name: "dashboard" });
}
</script>