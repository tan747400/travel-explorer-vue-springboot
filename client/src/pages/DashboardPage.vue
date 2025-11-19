<template>
  <div class="max-w-6xl mx-auto px-4 py-10">
    <h1 class="text-2xl md:text-3xl font-bold mb-4">
      ทริปของฉัน
    </h1>

    <p class="text-gray-600 mb-6">
      หน้านี้เอาไว้จัดการทริปที่คุณสร้างเอง (Create / Edit / Delete)
      ตอนนี้เริ่มจากการดึงรายการทริปของคุณจาก backend มาก่อน 😊
    </p>

    <!-- Loading -->
    <div v-if="loading" class="text-gray-500">
      กำลังโหลดข้อมูลทริปของคุณ...
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-red-500">
      {{ error }}
    </div>

    <!-- ไม่มีทริป -->
    <div
      v-else-if="trips.length === 0"
      class="border border-dashed border-gray-300 rounded-2xl p-6 text-center text-gray-500"
    >
      ยังไม่มีทริปที่คุณสร้างเลย ลองเริ่มสร้างทริปใหม่ดูไหม 🙂
    </div>

    <!-- มีทริปแล้ว -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <article
        v-for="trip in trips"
        :key="trip.id"
        class="rounded-xl border bg-white p-4 shadow-sm hover:shadow-md transition-shadow"
      >
        <h2 class="font-semibold text-lg mb-1">
          {{ trip.title }}
        </h2>

        <p class="text-sm text-sky-700 mb-1">
          {{ trip.destination }}
        </p>

        <p class="text-xs text-gray-500 mb-2">
          {{ formatDate(trip.startDate) }} - {{ formatDate(trip.endDate) }}
        </p>

        <p class="text-sm text-gray-700">
          {{ trip.description || "ไม่มีรายละเอียดเพิ่มเติม" }}
        </p>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/authStore";

interface Trip {
  id: number;
  title: string;
  destination: string;
  description: string | null;
  startDate: string | null;
  endDate: string | null;
}

const auth = useAuthStore();

const trips = ref<Trip[]>([]);
const loading = ref(false);
const error = ref("");

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function fetchMyTrips() {
  loading.value = true;
  error.value = "";

  try {
    const res = await fetch(`${API_BASE_URL}/api/trips/mine`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth.token}`, // ใช้ token จาก Pinia
      },
    });

    if (res.status === 401) {
      throw new Error("กรุณาเข้าสู่ระบบก่อนเข้าหน้านี้");
    }

    if (!res.ok) {
      throw new Error("โหลดทริปไม่สำเร็จ");
    }

    const data = await res.json();
    trips.value = data;
  } catch (err: any) {
    console.error(err);
    error.value = err.message || "เกิดข้อผิดพลาดบางอย่าง";
  } finally {
    loading.value = false;
  }
}

function formatDate(value: string | null) {
  if (!value) return "-";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleDateString("th-TH", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

onMounted(() => {
  fetchMyTrips();
});
</script>