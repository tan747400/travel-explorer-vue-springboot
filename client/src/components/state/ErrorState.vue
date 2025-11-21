<template>
  <div class="border border-red-200 bg-red-50 text-red-700 rounded-xl p-4 text-center">
    <p class="font-medium mb-1">เกิดข้อผิดพลาด</p>
    <p class="text-sm">
      {{ message || "ไม่สามารถโหลดข้อมูลได้ในขณะนี้" }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message?: string;
}>();
</script>