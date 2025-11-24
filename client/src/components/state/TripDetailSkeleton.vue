<template>
    <div class="animate-pulse space-y-6">
      <!-- Header skeleton -->
      <header
        class="flex flex-col md:flex-row md:items-end md:justify-between gap-3"
      >
        <div class="space-y-2">
          <div class="h-7 md:h-8 bg-slate-200 rounded w-48 md:w-72"></div>
          <div class="h-4 bg-slate-200 rounded w-32"></div>
          <div class="h-3 bg-slate-200 rounded w-40"></div>
        </div>
  
        <div class="flex flex-col items-end gap-2 w-full md:w-auto">
          <div class="h-4 bg-slate-200 rounded w-40 md:w-48"></div>
  
          <div class="hidden md:flex items-center gap-2">
            <div class="h-7 bg-slate-200 rounded-md w-24"></div>
            <div class="h-7 bg-slate-200 rounded-md w-24"></div>
          </div>
        </div>
      </header>
  
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Photos skeleton -->
        <section class="lg:col-span-2 space-y-4">
          <!-- Main image -->
          <div class="aspect-[16/9] rounded-2xl bg-slate-200"></div>
  
          <!-- Thumbnails -->
          <div class="grid grid-cols-3 gap-2">
            <div
              v-for="i in 3"
              :key="i"
              class="aspect-[4/3] rounded-lg bg-slate-200"
            ></div>
          </div>
        </section>
  
        <!-- Map skeleton -->
        <aside class="space-y-3">
          <div class="h-4 bg-slate-200 rounded w-24"></div>
  
          <div class="rounded-xl overflow-hidden border bg-white">
            <div class="w-full h-[260px] bg-slate-200"></div>
            <div class="p-3 border-t flex justify-end">
              <div class="h-3 bg-slate-200 rounded w-32"></div>
            </div>
          </div>
        </aside>
      </div>
  
      <!-- Description & tags skeleton -->
      <section class="mt-4 space-y-3">
        <div class="h-5 bg-slate-200 rounded w-36"></div>
  
        <div class="space-y-2 mt-2">
          <div class="h-3 bg-slate-200 rounded w-full"></div>
          <div class="h-3 bg-slate-200 rounded w-11/12"></div>
          <div class="h-3 bg-slate-200 rounded w-10/12"></div>
        </div>
  
        <div class="mt-4 flex flex-wrap gap-2">
          <span
            v-for="i in 4"
            :key="i"
            class="inline-flex h-6 px-6 rounded-full bg-slate-200"
          ></span>
        </div>
      </section>
    </div>
  </template>
  
  <script setup lang="ts">
  </script>
  
  <style scoped>
  </style>