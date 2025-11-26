<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-6xl mx-auto px-4 py-10">
      <!-- Header + ปุ่มเพิ่มทริป -->
      <div class="flex items-center justify-between mb-4">
        <h1 class="text-2xl md:text-3xl font-bold">
          ทริปของฉัน
        </h1>

        <!-- แสดงปุ่มเฉพาะตอนที่มีทริปแล้ว -->
        <button
          v-if="trips.length > 0"
          type="button"
          class="px-4 py-2 rounded-lg bg-sky-600 text-white text-sm hover:bg-sky-700"
          @click="goCreateTrip"
        >
          + เพิ่มทริปใหม่
        </button>
      </div>

      <!-- Loading: ใช้ Skeleton -->
      <DashboardSkeleton v-if="loading" />

      <!-- Error -->
      <ErrorState v-else-if="error" :message="error" />

      <!-- ยังไม่มีทริปเลย -->
      <section
        v-else-if="trips.length === 0"
        class="mt-10 flex justify-center"
      >
        <div
          class="w-full max-w-xl rounded-3xl border border-dashed border-sky-200
                 bg-gradient-to-br from-sky-50 via-white to-indigo-50
                 px-6 py-10 flex flex-col items-center text-center gap-4 shadow-sm"
        >
          <!-- วงกลมไอคอน -->
          <div
            class="h-20 w-20 rounded-full bg-white shadow flex items-center justify-center mb-1"
          >
            <span class="text-4xl">✈️</span>
          </div>

          <!-- ข้อความหลัก -->
          <div class="space-y-1">
            <h2 class="text-lg md:text-xl font-semibold text-slate-800">
              ยังไม่มีทริปที่คุณสร้างเลย
            </h2>
            <p class="text-sm text-slate-600">
              เริ่มบันทึกสถานที่ที่คุณชอบ หรือทริปในฝันของคุณไว้ที่นี่
              เพื่อให้กลับมาดู / แก้ไข / แชร์ได้ง่าย ๆ ในภายหลัง
            </p>
          </div>

          <!-- ตัวอย่างไอเดีย -->
          <ul class="text-xs text-slate-500 space-y-1">
            <li>• ทริปเที่ยวกับครอบครัว</li>
            <li>• คาเฟ่ / ร้านอาหารที่อยากกลับไปซ้ำ</li>
            <li>• ทริปในฝันที่ยังไม่ได้ไป แต่เริ่มวางแผนไว้ก่อนได้</li>
          </ul>

          <!-- ปุ่ม CTA -->
          <button
            type="button"
            class="mt-4 inline-flex items-center gap-2 px-5 py-2.5 rounded-full
                   bg-sky-600 text-white text-sm font-medium shadow hover:bg-sky-700
                   transition-colors"
            @click="goCreateTrip"
          >
            <span>เริ่มสร้างทริปแรกของคุณ</span>
            <span class="text-base">➜</span>
          </button>
        </div>
      </section>

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

          <!-- Tags -->
          <div
            v-if="trip.tags && trip.tags.length > 0"
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

          <!-- ปุ่มจัดการ -->
          <div class="mt-3 flex items-center justify-between gap-2 text-xs">
            <button
              type="button"
              class="px-3 py-1 rounded-md border text-sky-700 hover:bg-sky-50"
              @click="goToDetail(trip.id)"
            >
              ดูรายละเอียด
            </button>

            <div class="flex items-center gap-2">
              <button
                type="button"
                class="px-3 py-1 rounded-md border border-amber-300 text-amber-700 bg-amber-50 hover:bg-amber-100"
                @click="goToEdit(trip.id)"
              >
                แก้ไข
              </button>

              <button
                type="button"
                class="px-3 py-1 rounded-md border border-red-200 text-red-600 hover:bg-red-50 disabled:opacity-60 disabled:cursor-not-allowed"
                :disabled="deletingId === trip.id"
                @click="openDeleteModal(trip.id)"
              >
                {{ deletingId === trip.id ? "กำลังลบ..." : "ลบทริป" }}
              </button>
            </div>
          </div>
        </article>
      </div>

      <!-- Popup ลบทริป -->
      <Transition name="fade">
        <div
          v-if="showDeleteModal"
          class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
        >
          <div class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
            <h3 class="text-lg font-semibold text-slate-800 text-center">
              ต้องการลบทริปนี้จริง ๆ ไหม?
            </h3>
            <p class="mt-2 text-sm text-slate-500 text-center">
              การลบจะไม่สามารถกู้คืนได้
            </p>

            <div class="mt-6 flex items-center justify-center gap-3">
              <button
                type="button"
                class="px-4 py-2 rounded-lg border text-slate-600 hover:bg-slate-50"
                @click="closeDeleteModal"
              >
                ยกเลิก
              </button>

              <button
                type="button"
                class="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-60 disabled:cursor-not-allowed"
                :disabled="deletingId !== null"
                @click="confirmDeleteModal"
              >
                ลบทริป
              </button>
            </div>
          </div>
        </div>
      </Transition>
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

import ErrorState from "@/components/state/ErrorState.vue";
import DashboardSkeleton from "@/components/state/DashboardSkeleton.vue";

import { useToast } from "vue-toastification";
import { useSessionExpired } from "@/composables/useSessionExpired";

const toast = useToast();
const auth = useAuthStore();
const router = useRouter();
const { handleSessionExpired } = useSessionExpired();

const trips = ref<Trip[]>([]);
const loading = ref(false);
const error = ref("");
const deletingId = ref<number | null>(null);

// popup ลบทริป
const showDeleteModal = ref(false);
const tripToDelete = ref<number | null>(null);

async function fetchMyTrips() {
  loading.value = true;
  error.value = "";

  if (!auth.token) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    loading.value = false;
    return;
  }

  try {
    trips.value = await getMyTrips(auth.token);
  } catch (err: any) {
    console.error(err);

    if (err?.status === 401) {
      handleSessionExpired();
      return;
    }
    if (err?.status === 403) {
      const msg = "คุณสามารถดู/จัดการเฉพาะทริปของตัวเองเท่านั้น";
      error.value = msg;
      toast.error(msg);
      return;
    }

    const message = err.message || "เกิดข้อผิดพลาดขณะโหลดข้อมูลทริป";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
}

function goCreateTrip() {
  router.push({ name: "trip-create" });
}

function goToDetail(id: number) {
  router.push({
    name: "trip-detail",
    params: { id },
    query: { from: "dashboard" },
  });
}

function goToEdit(id: number) {
  router.push({ name: "trip-edit", params: { id } });
}

// เปิด popup ลบ
function openDeleteModal(id: number) {
  tripToDelete.value = id;
  showDeleteModal.value = true;
}

// ปิด popup ลบ
function closeDeleteModal() {
  showDeleteModal.value = false;
  tripToDelete.value = null;
}

// ยืนยันลบทริปจาก popup
async function confirmDeleteModal() {
  if (!tripToDelete.value) return;

  if (!auth.token) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  const id = tripToDelete.value;
  deletingId.value = id;
  error.value = "";

  try {
    await apiDeleteTrip(id, auth.token);
    trips.value = trips.value.filter((t) => t.id !== id);
    toast.success("ลบทริปสำเร็จแล้ว 🗑️");
  } catch (err: any) {
    console.error(err);

    if (err?.status === 401) {
      handleSessionExpired();
      return;
    }
    if (err?.status === 403) {
      toast.error("คุณไม่สามารถลบทริปของคนอื่นได้");
      return;
    }

    const message = err.message || "ลบทริปไม่สำเร็จ";
    error.value = message;
    toast.error(message);
  } finally {
    deletingId.value = null;
    closeDeleteModal();
  }
}

onMounted(fetchMyTrips);
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>