<template>
  <div class="max-w-6xl mx-auto px-4 py-10">
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-2xl md:text-3xl font-bold">ทริปของฉัน</h1>

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

    <Loading v-if="loading" />

    <ErrorState v-else-if="error" :message="error" />

    <EmptyState
      v-else-if="trips.length === 0"
      message="ยังไม่มีทริปที่คุณสร้างเลย ลองเริ่มสร้างทริปใหม่ดูไหม 🙂"
    />

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <article
        v-for="trip in trips"
        :key="trip.id"
        class="rounded-xl border bg-white p-4 shadow-sm hover:shadow-md transition-shadow"
      >
        <h2 class="font-semibold text-lg mb-1">{{ trip.title }}</h2>

        <p class="text-sm text-sky-700 mb-1">
          {{ trip.province || "ไม่ระบุสถานที่" }}
        </p>

        <p class="text-xs text-gray-500 mb-2">
          สร้างโดย: {{ trip.authorName || "-" }}
        </p>

        <p class="text-sm text-gray-700 line-clamp-2">
          {{ trip.description || "ไม่มีรายละเอียดเพิ่มเติม" }}
        </p>

        <div
          v-if="trip.tags?.length"
          class="mt-2 flex flex-wrap gap-2"
        >
          <span
            v-for="tag in trip.tags"
            :key="tag"
            class="inline-flex items-center rounded-full border border-sky-200 bg-sky-50 px-2 py-0.5 text-[11px] text-sky-700"
          >
            #{{ tag }}
          </span>
        </div>

        <div class="mt-3 flex items-center justify-between gap-2 text-xs">
          <button
            class="px-3 py-1 rounded-md border text-sky-700 hover:bg-sky-50"
            @click="goToDetail(trip.id)"
          >
            ดูรายละเอียด
          </button>

          <div class="flex items-center gap-2">
            <button
              class="px-3 py-1 rounded-md border border-amber-300 text-amber-700 bg-amber-50 hover:bg-amber-100"
              @click="goToEdit(trip.id)"
            >
              แก้ไข
            </button>

            <button
              class="px-3 py-1 rounded-md border border-red-200 text-red-600 hover:bg-red-50 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="deletingId === trip.id"
              @click="confirmDelete(trip.id)"
            >
              {{ deletingId === trip.id ? "กำลังลบ..." : "ลบทริป" }}
            </button>
          </div>
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
import {
  deleteTrip as apiDeleteTrip,
  getMyTrips,
} from "@/services/tripService";

import Loading from "@/components/state/Loading.vue";
import ErrorState from "@/components/state/ErrorState.vue";
import EmptyState from "@/components/state/EmptyState.vue";

import { useToast } from "vue-toastification";
const toast = useToast();

const auth = useAuthStore();
const router = useRouter();

const trips = ref<Trip[]>([]);
const loading = ref(false);
const error = ref("");
const deletingId = ref<number | null>(null);

async function fetchMyTrips() {
  loading.value = true;
  error.value = "";

  if (!auth.token) {
    handleExpired();
    return;
  }

  try {
    trips.value = await getMyTrips(auth.token);
  } catch (err: any) {
    console.error(err);

    if (err.status === 401) {
      handleExpired();
      return;
    }

    error.value = err.message || "เกิดข้อผิดพลาดขณะโหลดข้อมูลทริป";
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
}

function handleExpired() {
  const msg = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
  toast.error(msg);
  error.value = msg;
  auth.logout();
  router.push({ name: "login", query: { expired: "1" } });
}

function goCreateTrip() {
  router.push({ name: "trip-create" });
}

function goToDetail(id: number) {
  router.push({ name: "trip-detail", params: { id } });
}

function goToEdit(id: number) {
  router.push({ name: "trip-edit", params: { id } });
}

async function confirmDelete(id: number) {
  const ok = window.confirm("ต้องการลบทริปนี้จริง ๆ ใช่ไหม?");
  if (!ok) return;

  if (!auth.token) {
    handleExpired();
    return;
  }

  deletingId.value = id;

  try {
    await apiDeleteTrip(id, auth.token);
    trips.value = trips.value.filter((t) => t.id !== id);
    toast.success("ลบทริปสำเร็จแล้ว 🗑️");
  } catch (err: any) {
    if (err.status === 401) {
      handleExpired();
      return;
    }

    const msg = err.message || "ลบทริปไม่สำเร็จ";
    error.value = msg;
    toast.error(msg);
  } finally {
    deletingId.value = null;
  }
}

onMounted(fetchMyTrips);
</script>