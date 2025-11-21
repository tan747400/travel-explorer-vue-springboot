<template>
  <div class="min-h-screen bg-slate-50">
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

      <!-- States -->
      <Loading v-if="status === 'loading'" />
      <ErrorState v-else-if="status === 'error'" />
      <EmptyState v-else-if="status === 'success' && trips.length === 0" />

      <!-- Trip List -->
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

        <!-- Load more -->
        <div class="flex justify-center mt-6">
          <button
            v-if="!lastPage"
            type="button"
            class="px-4 py-2 rounded-lg border border-sky-500 text-sky-600 text-sm font-medium hover:bg-sky-50 disabled:opacity-60 disabled:cursor-not-allowed"
            :disabled="loadingMore"
            @click="fetchTrips(false)"
          >
            <span v-if="loadingMore">Loading...</span>
            <span v-else>Load more</span>
          </button>

          <p v-else class="text-sm text-gray-500">
            No more trips to load.
          </p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import SearchBar from "../components/SearchBar.vue";
import TripCard from "../components/TripCard.vue";
import Loading from "../components/state/Loading.vue";
import ErrorState from "../components/state/ErrorState.vue";
import EmptyState from "../components/state/EmptyState.vue";
import { getTrips } from "../services/tripService";
import type { Trip } from "../types/trip";
import { useDebouncedEffect } from "../composables/useDebouncedEffect";

type Status = "idle" | "loading" | "success" | "error";

const trips = ref<Trip[]>([]);
const status = ref<Status>("idle");
const keyword = ref("");

// Pagination state
const page = ref(0);
const size = ref(6);
const lastPage = ref(false);
const loadingMore = ref(false);

/**
 * ดึง trips จาก backend
 * reset = true  → โหลดหน้าแรกใหม่ (เปลี่ยน keyword)
 * reset = false → load more (เพิ่มหน้าใหม่)
 */
async function fetchTrips(reset: boolean) {
  try {
    if (reset) {
      trips.value = [];
      page.value = 0;
      lastPage.value = false;
      status.value = "loading";
    } else {
      loadingMore.value = true;
    }

    const res = await getTrips(keyword.value, page.value, size.value);

    if (reset) {
      trips.value = res.content;
    } else {
      trips.value = [...trips.value, ...res.content];
    }

    lastPage.value = res.last;
    page.value = res.number + 1;

    status.value = "success";
  } catch (err) {
    console.error(err);
    if (reset) status.value = "error";
  } finally {
    loadingMore.value = false;
  }
}

// Debounce keyword search
useDebouncedEffect(
  () => {
    fetchTrips(true);
  },
  [keyword],
  300
);

// Initial load
onMounted(() => {
  fetchTrips(true);
});

// จาก TripCard → เพิ่ม keyword อัตโนมัติ
function handleAddKeyword(tag: string) {
  const tokens = keyword.value.trim().split(" ").filter(Boolean);
  if (!tokens.includes(tag)) {
    keyword.value = keyword.value ? `${keyword.value} ${tag}` : tag;
  }
}
</script>