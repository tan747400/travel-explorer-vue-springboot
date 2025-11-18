<template>
  <div class="min-h-screen bg-slate-50">
    <!-- เนื้อหาหลัก “เที่ยวไหนดี + Search” -->
    <main class="px-3 md:px-10 py-10 max-w-6xl mx-auto">
      <h1
        class="text-5xl md:text-6xl text-center text-blue-400 font-bold mb-10"
      >
        เที่ยวไหนดี
      </h1>

      <!-- Search -->
      <div class="mb-10 w-full">
        <SearchBar
          v-model="keyword"
          label="ค้นหาที่เที่ยว"
          placeholder="หาที่เที่ยวแล้วไปกัน ..."
        />
      </div>

      <!-- Loading / Error -->
      <Loading v-if="status === 'loading'" />
      <ErrorState v-else-if="status === 'error'" />

      <!-- ฐานข้อมูลยังว่าง + ยังไม่ได้ค้นหาอะไรเลย -->
      <EmptyState
        v-else-if="status === 'success' && trips.length === 0 && !keyword"
      />

      <!-- ค้นหาแล้ว แต่ไม่เจอผลลัพธ์ -->
      <p
        v-else-if="status === 'success' && trips.length === 0 && keyword"
        class="text-center text-gray-500 py-10"
      >
        No trips found. Try a different keyword.
      </p>

      <!-- Trip list -->
      <div
        v-else-if="status === 'success' && trips.length > 0"
        class="flex flex-col gap-6 max-w-6xl mx-auto"
      >
        <TripCard
          v-for="item in trips"
          :key="item.id"
          :item="item"
          :keyword="keyword"
          @addKeyword="handleAddKeyword"
        />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import SearchBar from "@/components/SearchBar.vue";
import TripCard from "@/components/TripCard.vue";
import Loading from "@/components/state/Loading.vue";
import ErrorState from "@/components/state/ErrorState.vue";
import EmptyState from "@/components/state/EmptyState.vue";
import { getTrips } from "@/services/tripService";
import type { Trip } from "@/types/trip";
import { useDebouncedEffect } from "@/composables/useDebouncedEffect";

type Status = "idle" | "loading" | "success" | "error";

const trips = ref<Trip[]>([]);
const status = ref<Status>("idle");
const keyword = ref("");

async function fetchTrips() {
  try {
    status.value = "loading";
    const data = await getTrips(keyword.value);
    trips.value = data;
    status.value = "success";
  } catch (err) {
    console.error(err);
    status.value = "error";
  }
}

// debounce เวลาเปลี่ยน keyword (กันยิง API รัว ๆ)
useDebouncedEffect(fetchTrips, [keyword], 300);

function handleAddKeyword(tag: string) {
  const tokens = keyword.value
    .trim()
    .split(" ")
    .filter((t) => t.length > 0);

  if (!tokens.includes(tag)) {
    keyword.value = keyword.value ? `${keyword.value} ${tag}` : tag;
  }
}
</script>