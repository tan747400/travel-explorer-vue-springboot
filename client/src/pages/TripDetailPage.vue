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
            <!-- รูปหลัก -->
            <div v-if="coverPhoto" class="w-full h-64 md:h-80">
              <img
                :src="coverPhoto"
                :alt="trip.title"
                class="w-full h-full object-cover rounded-3xl"
              />
            </div>
  
            <!-- แกลลอรี่รูปย่อย -->
            <div
              v-if="otherPhotos.length"
              class="grid grid-cols-2 md:grid-cols-3 gap-2"
            >
              <img
                v-for="(photo, idx) in otherPhotos"
                :key="idx"
                :src="photo"
                :alt="`${trip.title} ${idx + 2}`"
                class="w-full h-24 md:h-28 object-cover rounded-xl"
              />
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
  
  const tripId = computed(() => Number(route.params.id));
  
  const coverPhoto = computed(() =>
    trip.value?.photos?.length ? trip.value.photos[0] : null
  );
  
  const otherPhotos = computed(
    () => trip.value?.photos?.slice(1, 4) ?? []
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
    } catch (err) {
      console.error(err);
      status.value = "error";
    }
  }
  
  onMounted(fetchTrip);
  </script>  