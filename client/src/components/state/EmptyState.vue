<template>
  <div class="text-center py-10 text-gray-500">
    <div class="mx-auto mb-3 w-24 opacity-80">
      <!-- Cute empty icon -->
      <svg viewBox="0 0 24 24" fill="none" class="w-full h-full">
        <path
          d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10"
          stroke="#93c5fd"
          stroke-width="2"
          stroke-linecap="round"
        />
        <path
          d="M8 15s1.5 2 4 2 4-2 4-2"
          stroke="#3b82f6"
          stroke-width="2"
          stroke-linecap="round"
        />
      </svg>
    </div>

    <p class="text-sm">
      {{ message || "ยังไม่มีข้อมูลในตอนนี้" }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message?: string;
}>();
</script>