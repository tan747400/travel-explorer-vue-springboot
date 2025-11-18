<template>
    <article
      class="relative bg-white rounded-3xl shadow-sm p-4 md:p-6 flex flex-col md:flex-row gap-4 md:gap-6"
    >
      <!-- รูปหลัก -->
      <div class="md:w-64 md:h-40 w-full h-52 shrink-0">
        <img
          :src="coverPhoto"
          alt=""
          class="w-full h-full object-cover rounded-2xl"
        />
      </div>
  
      <!-- เนื้อหา -->
      <div class="flex-1 flex flex-col gap-2">
        <h2 class="text-xl md:text-2xl font-semibold text-gray-900">
          {{ item.title }}
        </h2>
  
        <p class="text-sm text-gray-500 line-clamp-3">
          {{ shortDescription }}
        </p>
  
        <!-- tags -->
        <div class="flex flex-wrap gap-2 text-xs md:text-sm text-sky-600 mt-2">
          <button
            v-for="tag in item.tags"
            :key="tag"
            class="underline hover:text-sky-700"
            @click="$emit('addKeyword', tag)"
          >
            {{ tag }}
          </button>
        </div>
  
        <!-- ปุ่ม view detail -->
        <div class="mt-3">
          <button
            class="text-sky-600 text-sm md:text-base underline font-medium"
          >
            View Detail
          </button>
        </div>
      </div>
  
      <!-- ปุ่ม copy link -->
      <CopyButton :url="detailUrl" />
    </article>
  </template>
  
  <script setup lang="ts">
  import { computed } from "vue";
  import CopyButton from "./CopyButton.vue";
  import type { Trip } from "../types/trip";
  
  const props = defineProps<{
    item: Trip;
  }>();
  
  defineEmits<{
    (e: "addKeyword", tag: string): void;
  }>();
  
  const coverPhoto = computed(
    () => props.item.photos?.[0] ?? "https://placehold.co/600x400"
  );
  
  const shortDescription = computed(() => {
    if (!props.item.description) return "";
    return props.item.description.length > 120
      ? props.item.description.slice(0, 120) + "..."
      : props.item.description;
  });
  
  const detailUrl = computed(() => `/trips/${props.item.id}`);
  </script>  