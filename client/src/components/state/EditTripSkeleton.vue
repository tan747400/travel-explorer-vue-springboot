<template>
    <section
      class="bg-white rounded-2xl border border-slate-200 shadow-sm px-4 sm:px-6 py-5 animate-pulse"
    >
      <div class="space-y-4">
        <!-- Title -->
        <div class="space-y-2">
          <div class="h-4 bg-slate-200 rounded w-28"></div>
          <div class="h-9 bg-slate-200 rounded"></div>
        </div>
  
        <!-- Province -->
        <div class="space-y-2">
          <div class="h-4 bg-slate-200 rounded w-24"></div>
          <div class="h-9 bg-slate-200 rounded"></div>
        </div>
  
        <!-- Description -->
        <div class="space-y-2">
          <div class="h-4 bg-slate-200 rounded w-28"></div>
          <div class="space-y-2">
            <div class="h-20 bg-slate-200 rounded"></div>
            <div class="flex items-center justify-between">
              <div class="h-3 bg-slate-200 rounded w-24"></div>
              <div class="h-3 bg-slate-200 rounded w-16"></div>
            </div>
          </div>
        </div>
  
        <!-- Tags -->
        <div class="space-y-2">
          <div class="h-4 bg-slate-200 rounded w-40"></div>
          <div class="h-9 bg-slate-200 rounded"></div>
          <div class="h-3 bg-slate-200 rounded w-48"></div>
        </div>
  
        <!-- Lat / Lng -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="space-y-2">
            <div class="h-4 bg-slate-200 rounded w-20"></div>
            <div class="h-9 bg-slate-200 rounded"></div>
          </div>
          <div class="space-y-2">
            <div class="h-4 bg-slate-200 rounded w-20"></div>
            <div class="h-9 bg-slate-200 rounded"></div>
          </div>
        </div>
  
        <!-- Map preview -->
        <div class="mt-2 space-y-2">
          <div class="h-4 bg-slate-200 rounded w-24"></div>
          <div class="h-32 bg-slate-200 rounded-xl"></div>
        </div>
  
        <!-- Photos section -->
        <section class="mt-4 border-t border-slate-200 pt-4 space-y-3">
          <div class="h-4 bg-slate-200 rounded w-28"></div>
  
          <div class="flex flex-wrap gap-2">
            <div
              v-for="i in 4"
              :key="i"
              class="w-24 h-16 bg-slate-200 rounded-lg"
            ></div>
          </div>
  
          <div class="space-y-2 max-w-md mt-2">
            <div class="h-9 bg-slate-200 rounded"></div>
            <div class="h-9 bg-slate-200 rounded w-40"></div>
          </div>
        </section>
  
        <!-- Buttons -->
        <div class="flex items-center gap-3 pt-2">
          <div class="h-9 bg-slate-200 rounded-md w-40"></div>
          <div class="h-9 bg-slate-200 rounded-md w-28"></div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup lang="ts">
  </script>
  
  <style scoped>
  </style>