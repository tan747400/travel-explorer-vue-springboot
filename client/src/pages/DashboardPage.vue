<template>
  <div class="max-w-6xl mx-auto px-4 py-10">
    <!-- Header + ปุ่มเพิ่มทริป -->
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-2xl md:text-3xl font-bold">
        ทริปของฉัน
      </h1>

      <button
        type="button"
        class="px-4 py-2 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-700"
        @click="goCreateTrip"
      >
        + เพิ่มทริปใหม่
      </button>
    </div>

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
          {{ trip.province || "ไม่ระบุสถานที่" }}
        </p>

        <p class="text-xs text-gray-500 mb-2">
          สร้างโดย: {{ trip.authorName || "-" }}
        </p>

        <p class="text-sm text-gray-700 line-clamp-2">
          {{ trip.description || "ไม่มีรายละเอียดเพิ่มเติม" }}
        </p>

        <!-- ปุ่มจัดการ -->
        <div class="mt-3 flex items-center justify-between gap-2 text-xs">
          <button
            type="button"
            class="px-3 py-1 rounded-md border text-sky-700 hover:bg-sky-50"
            @click="goToDetail(trip.id)"
          >
            ดูรายละเอียด
          </button>

          <button
            type="button"
            class="px-3 py-1 rounded-md border border-red-200 text-red-600 hover:bg-red-50 disabled:opacity-60 disabled:cursor-not-allowed"
            :disabled="deletingId === trip.id"
            @click="confirmDelete(trip.id)"
          >
            {{ deletingId === trip.id ? "กำลังลบ..." : "ลบทริป" }}
          </button>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import type { Trip } from "@/types/trip";
import { deleteTrip as apiDeleteTrip } from "@/services/tripService";

const auth = useAuthStore();
const router = useRouter();

const trips = ref<Trip[]>([]);
const loading = ref(false);
const error = ref("");
const deletingId = ref<number | null>(null);

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function fetchMyTrips() {
  loading.value = true;
  error.value = "";

  if (!auth.token) {
    error.value = "ไม่พบโทเคน กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    loading.value = false;
    return;
  }

  try {
    const res = await fetch(`${API_BASE_URL}/api/trips/mine`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth.token}`,
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

function goCreateTrip() {
  router.push({ name: "trip-create" });
}

function goToDetail(id: number) {
  router.push({ name: "trip-detail", params: { id } });
}

async function confirmDelete(id: number) {
  const ok = window.confirm("ต้องการลบทริปนี้จริง ๆ ใช่ไหม?");
  if (!ok) return;

  if (!auth.token) {
    error.value = "ไม่พบโทเคน กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    return;
  }

  deletingId.value = id;
  error.value = "";

  try {
    await apiDeleteTrip(id, auth.token);
    // ลบ card ออกจาก list บนหน้า
    trips.value = trips.value.filter((t) => t.id !== id);
  } catch (err: any) {
    console.error(err);
    error.value = err.message || "ลบทริปไม่สำเร็จ";
  } finally {
    deletingId.value = null;
  }
}

onMounted(() => {
  fetchMyTrips();
});
</script>