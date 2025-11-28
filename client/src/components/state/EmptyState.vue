<template>
  <div class="text-center py-12 text-slate-500">
    <div class="mx-auto mb-4 w-20 opacity-90">
      <!-- Cute empty icon -->
      <svg viewBox="0 0 24 24" fill="none" class="w-full h-full">
        <circle
          cx="12"
          cy="12"
          r="10"
          stroke="#93c5fd"
          stroke-width="2"
        />
        <path
          d="M8 15c1 1.2 2.5 2 4 2s3-0.8 4-2"
          stroke="#3b82f6"
          stroke-width="2"
          stroke-linecap="round"
        />
      </svg>
    </div>

    <p class="text-sm text-slate-600">
      {{ message || "ยังไม่มีข้อมูลในตอนนี้" }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message?: string;
}>();
</script>