<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getTrips } from "@/services/tripService";
import type { Trip } from "@/types/trip";

const trips = ref<Trip[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const router = useRouter();

/**
 * ตัด description ให้สั้น ~120 ตัวอักษร
 */
function shortDescription(text?: string | null) {
  if (!text) return "";
  const trimmed = text.trim();
  return trimmed.length > 120 ? trimmed.slice(0, 120) + "…" : trimmed;
}

function goToDetail(id: number) {
  router.push(`/trips/${id}`);
}

onMounted(async () => {
  try {
    loading.value = true;
    error.value = null;
    // ดึงข้อมูลจาก backend: GET /api/trips
    trips.value = await getTrips();
  } catch (err: any) {
    console.error(err);
    error.value = "Error fetching trips.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <!-- ===== Navbar (แบบง่าย ๆ ก่อน) ===== -->
    <header class="border-b bg-white">
      <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        <div class="font-bold text-xl text-sky-700">
          Travel Explorer
        </div>

        <nav class="flex items-center gap-3">
          <button
            type="button"
            class="text-sm px-3 py-1.5 rounded-md border border-sky-500 text-sky-600 hover:bg-sky-50"
          >
            Login
          </button>
          <button
            type="button"
            class="text-sm px-3 py-1.5 rounded-md bg-sky-600 text-white hover:bg-sky-700"
          >
            Register
          </button>
        </nav>
      </div>
    </header>

    <!-- ===== Main Content ===== -->
    <main class="max-w-6xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-semibold mb-2">Destinations</h1>
      <p class="text-gray-600 mb-6">
        Discover trips from all around Thailand and the world.
      </p>

      <!-- error state -->
      <p v-if="error" class="text-red-600 mb-4">
        {{ error }}
      </p>

      <!-- loading state -->
      <div v-if="loading" class="text-gray-500">
        Loading trips...
      </div>

      <!-- empty state -->
      <div
        v-else-if="trips.length === 0"
        class="text-center text-gray-500 py-16"
      >
        <p class="text-lg font-semibold mb-2">
          No trips have been added yet.
        </p>
        <p>Be the first to share your favorite spot!</p>
      </div>

      <!-- list of trips -->
      <div v-else class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <article
          v-for="trip in trips"
          :key="trip.id"
          class="bg-white rounded-xl shadow-sm overflow-hidden flex flex-col"
        >
          <!-- รูป cover -->
          <div class="h-44 bg-gray-200 overflow-hidden">
            <img
              v-if="trip.photos && trip.photos.length > 0"
              :src="trip.photos[0]"
              :alt="trip.title"
              class="w-full h-full object-cover"
            />
            <div
              v-else
              class="w-full h-full flex items-center justify-center text-gray-400 text-sm"
            >
              No image
            </div>
          </div>

          <!-- เนื้อหา card -->
          <div class="p-4 flex flex-col flex-1">
            <h2 class="font-semibold text-lg mb-1 line-clamp-2">
              {{ trip.title }}
            </h2>

            <!-- province -->
            <p
              v-if="trip.province"
              class="text-sm text-blue-600 font-medium mb-2"
            >
              {{ trip.province }}
            </p>

            <!-- short description -->
            <p class="text-sm text-gray-600 flex-1">
              {{ shortDescription(trip.description) }}
            </p>

            <!-- View Detail -->
            <button
              class="mt-4 inline-flex items-center justify-center rounded-lg border border-sky-500 text-sky-600 text-sm font-medium px-3 py-2 hover:bg-sky-50"
              type="button"
              @click="goToDetail(trip.id)"
            >
              View Detail
            </button>
          </div>
        </article>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* ถ้าในโปรเจกต์ยังไม่ได้ใช้ Tailwind ก็ไม่เป็นไร
   class ด้านบนจะไม่เจ๊ง แค่ไม่สวยมาก
   หรือจะลบ style ตรงนี้ทิ้งแล้วใช้ CSS ของตัวเองก็ได้ */

/* ถ้าใช้ Tailwind + line-clamp ต้องเช็กว่าติดตั้ง plugin แล้ว
   ถ้าไม่มี plugin ก็เอา class line-clamp-2 ออกได้ */
</style>
