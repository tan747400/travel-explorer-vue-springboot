<template>
  <div class="rounded-xl border border-red-200 bg-red-50 px-5 py-4 text-center">
    <!-- Icon -->
    <div class="flex justify-center mb-2">
      <div class="h-8 w-8 rounded-full bg-red-100 flex items-center justify-center text-red-600">
        !
      </div>
    </div>

    <p class="font-medium text-red-700 mb-1">เกิดข้อผิดพลาด</p>

    <p class="text-sm text-red-600">
      {{ message || "ไม่สามารถโหลดข้อมูลได้ในขณะนี้ กรุณาลองใหม่อีกครั้ง" }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message?: string;
}>();
</script>