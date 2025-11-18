<template>
  <div class="px-3 md:px-10 py-10 max-w-6xl mx-auto">
    <!-- Loading / Error -->
    <Loading v-if="status === 'loading'" />
    <ErrorState v-else-if="status === 'error'" />

    <template v-else-if="trip">
      <!-- ชื่อ + ที่อยู่ -->
      <header class="mb-6">
        <h1 class="text-3xl md:text-4xl font-bold mb-2">
          {{ trip.title }}
        </h1>
        <p class="text-gray-600 text-sm md:text-base">
          {{ trip.address || trip.province || "ไม่มีข้อมูลที่อยู่" }}
        </p>
      </header>

      <!-- layout 2 คอลัมน์บน desktop -->
      <div class="flex flex-col md:flex-row gap-8">
        <!-- ซ้าย: รูป + รายละเอียด -->
        <section class="md:w-2/3 flex flex-col gap-4">
          <!-- รูปหลัก (เปลี่ยนตามรูปที่คลิก) -->
          <div v-if="mainPhoto" class="w-full h-64 md:h-80">
            <img
              :src="mainPhoto"
              :alt="trip.title"
              class="w-full h-full object-cover rounded-3xl"
            />
          </div>

          <!-- แกลลอรี่รูปย่อย (คลิกเพื่อเปลี่ยนรูปหลัก) -->
          <div
            v-if="thumbnailPhotos.length"
            class="grid grid-cols-2 md:grid-cols-3 gap-2"
          >
            <button
              v-for="thumb in thumbnailPhotos"
              :key="thumb.idx"
              type="button"
              class="relative w-full h-24 md:h-28 rounded-xl overflow-hidden focus:outline-none focus:ring-2 focus:ring-sky-400"
              @click="selectPhoto(thumb.idx)"
            >
              <img
                :src="thumb.src"
                :alt="`${trip.title} ${thumb.idx + 1}`"
                class="w-full h-full object-cover"
              />
              <!-- เส้นขอบบาง ๆ เวลา hover -->
              <span
                class="pointer-events-none absolute inset-0 border border-transparent hover:border-white/80"
              />
            </button>
          </div>

          <!-- คำอธิบาย -->
          <div>
            <h2 class="font-semibold mb-2">รายละเอียดสถานที่</h2>
            <p class="text-gray-700 leading-relaxed whitespace-pre-line">
              {{ trip.description || "ไม่มีคำอธิบาย" }}
            </p>
          </div>

          <!-- tags -->
          <div v-if="trip.tags?.length" class="mt-2 flex flex-wrap gap-2">
            <span
              v-for="tag in trip.tags"
              :key="tag"
              class="px-3 py-1 bg-sky-50 text-sky-700 text-xs rounded-full"
            >
              {{ tag }}
            </span>
          </div>
        </section>

        <!-- ขวา: แผนที่ -->
        <aside class="md:w-1/3">
          <h2 class="font-semibold mb-3">แผนที่</h2>

          <div v-if="hasCoordinates" class="space-y-3">
            <div class="w-full h-64 border rounded-xl overflow-hidden">
              <iframe
                class="w-full h-full"
                :src="mapEmbedUrl"
                style="border:0;"
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade"
              />
            </div>

            <a
              :href="googleMapsLink"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex items-center text-sky-600 underline text-sm"
            >
              View on Google Maps
            </a>
          </div>

          <p v-else class="text-gray-500 text-sm">
            Map information not available for this destination.
          </p>
        </aside>
      </div>
    </template>

    <EmptyState v-else />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import Loading from "../components/state/Loading.vue";
import ErrorState from "../components/state/ErrorState.vue";
import EmptyState from "../components/state/EmptyState.vue";
import { getTripById } from "../services/tripService";
import type { Trip } from "../types/trip";

type Status = "idle" | "loading" | "success" | "error";

const route = useRoute();
const trip = ref<Trip | null>(null);
const status = ref<Status>("idle");

// index ของรูปที่ถูกเลือก (0 = รูปแรก)
const activePhotoIndex = ref(0);

const tripId = computed(() => Number(route.params.id));

// ลิสต์รูปทั้งหมด
const photos = computed(() => trip.value?.photos ?? []);

// รูปหลักที่แสดงด้านบน
const mainPhoto = computed(() =>
  photos.value.length > 0 ? photos.value[activePhotoIndex.value] : null
);

// รูป thumbnail ด้านล่าง (เอาทุกรูปที่ไม่ใช่รูปหลัก แล้วจำกัดสัก 3 รูปเหมือนเดิม)
const thumbnailPhotos = computed(() =>
  photos.value
    .map((src, idx) => ({ src, idx }))
    .filter((p) => p.idx !== activePhotoIndex.value)
    .slice(0, 3)
);

const hasCoordinates = computed(
  () =>
    !!trip.value &&
    trip.value.latitude != null &&
    trip.value.longitude != null
);

const mapEmbedUrl = computed(() => {
  if (!hasCoordinates.value || !trip.value) return "";
  const { latitude, longitude } = trip.value;
  return `https://www.google.com/maps?q=${latitude},${longitude}&hl=th&z=14&output=embed`;
});

const googleMapsLink = computed(() => {
  if (!hasCoordinates.value || !trip.value) return "#";
  const { latitude, longitude } = trip.value;
  return `https://www.google.com/maps?q=${latitude},${longitude}`;
});

async function fetchTrip() {
  try {
    status.value = "loading";
    const data = await getTripById(tripId.value);
    trip.value = data;
    status.value = "success";

    // โหลดเสร็จให้ default เป็นรูปแรกเสมอ
    activePhotoIndex.value = 0;
  } catch (err) {
    console.error(err);
    status.value = "error";
  }
}

// เวลา user คลิกที่รูปย่อย → เปลี่ยน index
function selectPhoto(idx: number) {
  activePhotoIndex.value = idx;
}

onMounted(fetchTrip);
</script>