<template>
  <section
    class="flex flex-col gap-4 sm:gap-5"
    aria-hidden="true"
  >
    <!-- โครง Card จำลอง N ใบ (default 3) -->
    <div
      v-for="n in count"
      :key="n"
      class="relative flex flex-col md:flex-row items-start gap-4 p-4 md:p-5
             bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden"
    >
      <!-- shimmer overlay -->
      <div class="absolute inset-0 pointer-events-none skeleton-shimmer" />

      <!-- รูปหลัก -->
      <div
        class="w-full md:w-[320px] h-[220px] rounded-2xl bg-slate-200/80 flex-shrink-0"
      ></div>

      <!-- เนื้อหา -->
      <div class="flex-1 flex flex-col gap-2">
        <!-- title line -->
        <div class="h-4 sm:h-5 w-4/5 rounded-full bg-slate-200/80"></div>

        <!-- meta line -->
        <div class="h-3 w-1/3 rounded-full bg-slate-200/70"></div>

        <!-- description lines -->
        <div class="mt-1 space-y-2">
          <div class="h-3 w-full rounded-full bg-slate-200/80"></div>
          <div class="h-3 w-11/12 rounded-full bg-slate-200/70"></div>
          <div class="h-3 w-2/3 rounded-full bg-slate-200/60"></div>
        </div>

        <!-- tag chips -->
        <div class="mt-3 flex flex-wrap gap-2">
          <span
            v-for="i in 3"
            :key="i"
            class="h-6 w-16 rounded-full bg-slate-200/80"
          ></span>
        </div>

        <!-- photo grid placeholder -->
        <div class="mt-3 grid grid-cols-3 gap-2 max-w-xs">
          <div
            v-for="i in 3"
            :key="i"
            class="h-16 rounded-lg bg-slate-200/80"
          ></div>
        </div>
      </div>
    </div>

    <!-- ข้อความเล็ก ๆ ด้านล่าง -->
    <p class="text-center text-xs text-slate-400 mt-2">
      กำลังโหลดทริปให้คุณ...
    </p>
  </section>
</template>

<script setup lang="ts">
const props = defineProps<{
  count?: number;
}>();

// default ให้เป็น 3 ถ้าไม่ได้ส่งมา
const count = props.count ?? 3;
</script>

<style scoped>
/* shimmer effect บาง ๆ ให้ card ดูมีชีวิต */
.skeleton-shimmer {
  background-image: linear-gradient(
    120deg,
    rgba(226, 232, 240, 0.3) 0%,
    rgba(226, 232, 240, 0.9) 20%,
    rgba(226, 232, 240, 0.3) 40%
  );
  background-size: 200% 100%;
  animation: shimmer-move 1.4s ease-in-out infinite;
  mix-blend-mode: soft-light;
}

@keyframes shimmer-move {
  0% {
    transform: translateX(-40%);
  }
  100% {
    transform: translateX(40%);
  }
}
</style>