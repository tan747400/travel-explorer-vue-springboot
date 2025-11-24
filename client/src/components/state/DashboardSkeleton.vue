<template>
    <section class="mt-4">
      <!-- Header skeleton -->
      <div class="flex items-center justify-between mb-4">
        <div class="h-7 w-36 rounded-full bg-slate-200/80"></div>
        <div class="h-9 w-32 rounded-full bg-slate-200/80"></div>
      </div>
  
      <!-- Grid skeleton cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <article
          v-for="n in 4"
          :key="n"
          class="relative rounded-xl border border-slate-100 bg-white p-4 shadow-sm overflow-hidden"
        >
          <!-- shimmer overlay -->
          <div class="absolute inset-0 pointer-events-none skeleton-shimmer"></div>
  
          <!-- title -->
          <div class="h-4 w-2/3 rounded-full bg-slate-200/85 mb-2"></div>
  
          <!-- province + author -->
          <div class="space-y-1 mb-3">
            <div class="h-3 w-1/3 rounded-full bg-slate-200/80"></div>
            <div class="h-3 w-1/2 rounded-full bg-slate-200/70"></div>
          </div>
  
          <!-- description -->
          <div class="space-y-2 mb-3">
            <div class="h-3 w-full rounded-full bg-slate-200/80"></div>
            <div class="h-3 w-11/12 rounded-full bg-slate-200/70"></div>
            <div class="h-3 w-2/3 rounded-full bg-slate-200/60"></div>
          </div>
  
          <!-- tags -->
          <div class="flex flex-wrap gap-2 mb-3">
            <div
              v-for="i in 3"
              :key="i"
              class="h-5 w-16 rounded-full bg-slate-200/80"
            ></div>
          </div>
  
          <!-- buttons -->
          <div class="flex items-center justify-between pt-2">
            <div class="h-8 w-24 rounded-md bg-slate-200/80"></div>
            <div class="flex gap-2">
              <div class="h-8 w-20 rounded-md bg-slate-200/80"></div>
              <div class="h-8 w-20 rounded-md bg-slate-200/80"></div>
            </div>
          </div>
        </article>
      </div>
  
      <p class="text-center text-xs text-slate-400 mt-4">
        กำลังโหลดทริปของคุณ...
      </p>
    </section>
  </template>
  
  <script setup lang="ts">
  </script>
  
  <style scoped>
  .skeleton-shimmer {
    background-image: linear-gradient(
      120deg,
      rgba(226, 232, 240, 0.25) 0%,
      rgba(226, 232, 240, 0.9) 25%,
      rgba(226, 232, 240, 0.25) 50%
    );
    background-size: 200% 100%;
    animation: shimmer-move 1.4s ease-in-out infinite;
    mix-blend-mode: soft-light;
  }
  
  @keyframes shimmer-move {
    0% {
      transform: translateX(-35%);
    }
    100% {
      transform: translateX(35%);
    }
  }
  </style>  