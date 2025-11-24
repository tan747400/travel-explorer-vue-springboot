<template>
    <section class="space-y-6">
      <!-- Heading skeleton -->
      <div class="flex flex-col items-center mb-4">
        <div class="h-9 w-52 sm:w-64 rounded-full bg-slate-200/80 mb-2"></div>
        <div class="h-3 w-64 sm:w-80 rounded-full bg-slate-200/70"></div>
      </div>
  
      <!-- Search skeleton -->
      <div class="w-full max-w-xl mx-auto mb-4">
        <div class="h-3 w-20 rounded-full bg-slate-200/80 mb-2"></div>
        <div class="h-10 w-full rounded-xl bg-slate-200/80"></div>
      </div>
  
      <!-- Filters skeleton -->
      <section
        class="mb-6 bg-white rounded-2xl border border-slate-200 shadow-sm px-4 sm:px-6 py-4 relative overflow-hidden"
      >
        <!-- shimmer overlay -->
        <div class="absolute inset-0 pointer-events-none home-skeleton-shimmer"></div>
  
        <!-- Province row -->
        <div class="flex flex-wrap items-center gap-3 text-xs sm:text-sm mb-4">
          <div class="h-3 w-24 rounded-full bg-slate-200/80"></div>
          <div class="h-8 w-40 rounded-lg bg-slate-200/80"></div>
        </div>
  
        <!-- Tags row -->
        <div class="flex flex-col gap-3 w-full">
          <div class="flex justify-between items-center">
            <div class="h-3 w-10 rounded-full bg-slate-200/80"></div>
            <div class="h-3 w-20 rounded-full bg-slate-200/70"></div>
          </div>
  
          <div class="flex flex-wrap gap-2 mt-1">
            <div
              v-for="n in 8"
              :key="n"
              class="h-7 w-16 rounded-full bg-slate-200/80"
            ></div>
          </div>
        </div>
      </section>
  
      <!-- Trip list skeleton (reuse component เดิม) -->
      <TripListSkeleton />
    </section>
  </template>
  
  <script setup lang="ts">
  import TripListSkeleton from "@/components/state/TripListSkeleton.vue";
  </script>
  
  <style scoped>
  .home-skeleton-shimmer {
    background-image: linear-gradient(
      120deg,
      rgba(226, 232, 240, 0.2) 0%,
      rgba(226, 232, 240, 0.9) 30%,
      rgba(226, 232, 240, 0.2) 60%
    );
    background-size: 200% 100%;
    animation: home-shimmer-move 1.4s ease-in-out infinite;
    mix-blend-mode: soft-light;
  }
  
  @keyframes home-shimmer-move {
    0% {
      transform: translateX(-30%);
    }
    100% {
      transform: translateX(30%);
    }
  }
  </style>  